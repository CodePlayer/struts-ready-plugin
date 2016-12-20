package me.codeplayer.auth;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;

import me.codeplayer.annotation.*;

/**
 * 默认的权限策略（用于配置权限码的生成策略等）
 * 
 * @author Ready
 * @date 2015年2月4日
 */
public class DefaultPermissionPolicy implements PermissionPolicy {

	//
	protected String basePackage;
	protected int baseIndex;

	public String getBasePackage() {
		return basePackage;
	}

	@Inject("struts.convention.action.packages")
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
		baseIndex = basePackage.length() + 1;
	}

	public String getCodeFromClass(ActionInvocation invocation, Class<?> clazz, Permission permission) {
		return clazz.getName().substring(baseIndex);
	}

	public String getCodeFromMethod(ActionInvocation invocation, Method method, Permission permission) {
		String permissionCode = null;
		final Menu[] menus = permission.menus();
		if (menus.length > 1) {
			final StringBuilder permissionCodeBuilder = new StringBuilder(method.getDeclaringClass().getName().substring(baseIndex)).append('.').append(method.getName());
			// 如果有@Menus注解，并且有多个@Menu注解
			// 则该方法对应多个菜单、多个权限码：权限码=方法的默认权限码 + 数字后缀(索引或suffix参数值)
			// 后缀为 0 时，不追加后缀
			final HttpServletRequest request = (HttpServletRequest) invocation.getInvocationContext().get(StrutsStatics.HTTP_REQUEST);
			int menuIndex = -1, suffix = -1;
			for (int i = 0; i < menus.length; i++) {
				Menu menu = menus[i];
				if (menu.suffix() > Menu.DEFAULT_SUFFIX) {
					suffix = menu.suffix();
				} else {
					suffix++;
				}
				String[] args = menu.args(); // 菜单所需校验的额外请求参数的键值对数组
				if (args.length > 0) {
					if ((args.length & 1) != 0) { // 数组长度必须为偶数
						throw new IllegalArgumentException("The number of arguments passed in args() must be even!");
					}
					boolean isCurrent = true;
					for (int j = 0; j < args.length; j++) {
						String value = request.getParameter(args[j++]);
						if (value == null || value.length() == 0) {
							isCurrent = false;
							break;
						}
						String expected = args[j];
						if ("*".equals(expected) || value.equals(expected)) { // 如果 args()的参数值为"*"，则不校验具体的值
							continue;
						} else {
							isCurrent = false;
							break;
						}
					}
					if (isCurrent) {
						menuIndex = i;
						break;
					}
				} else {
					menuIndex = i;
					break;
				}
			}
			if (menuIndex != -1) {
				setTitle(request, menus[menuIndex]);
				if (suffix > 0) { // 如果大于0才添加后缀
					permissionCodeBuilder.append('-').append(suffix);
				}
				permissionCode = permissionCodeBuilder.toString();
			}
		}
		return permissionCode;
	}

	/**
	 * 设置标题
	 * 
	 * @param request
	 * @param currentMenu
	 */
	public static final void setTitle(final HttpServletRequest request, final Menu currentMenu) {
		final String title = currentMenu.name();
		if (title.length() > 0) {
			request.setAttribute(Ready.TITLE_KEY, title);
		}
	}
}
