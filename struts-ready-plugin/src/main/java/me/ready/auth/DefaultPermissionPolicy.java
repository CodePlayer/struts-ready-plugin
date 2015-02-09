package me.ready.auth;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import me.ready.annotation.Menu;
import me.ready.annotation.Menus;
import me.ready.util.StringUtil;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;

/**
 * 默认的权限策略（用于配置权限码的生成策略等）
 * 
 * @package me.ready.auth
 * @author Ready
 * @date 2015年2月4日
 * @since
 * 
 */
public class DefaultPermissionPolicy implements PermissionPolicy {

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

	public String getCodeFromClass(ActionInvocation actionInvocation, Class<?> clazz) {
		return clazz.getName().substring(baseIndex);
	}

	public String getCodeFromMethod(ActionInvocation actionInvocation, Method method) {
		String permissionCode = null;
		Menus menus = method.getAnnotation(Menus.class);
		if (menus != null && menus.value().length > 1) {
			// 如果有@Menus注解，并且有多个@Menu注解，则该方法对应多个菜单、多个权限码：权限码=方法的默认权限码 + 数字后缀(索引或order参数值)
			HttpServletRequest request = ServletActionContext.getRequest();
			Menu[] menuArray = menus.value();
			int menuIndex = -1;
			for (int i = 0; i < menuArray.length; i++) {
				Menu menu = menuArray[i];
				String[] args = menu.args(); // 菜单所需校验的额外请求参数的键值对数组
				if (args.length > 0) {
					if ((args.length & 1) != 0) { // 数组长度必须为偶数
						throw new IllegalArgumentException("The number of arguments passed in args() must be even!");
					}
					boolean isCurrent = true;
					for (int j = 0; j < args.length; j++) {
						String value = request.getParameter(args[j++]);
						if (StringUtil.isEmpty(value)) {
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
				setTitle(request, menuArray[menuIndex]);
				if (menuArray[menuIndex].order() != Menu.DEFAULT_ORDER) {
					menuIndex = menuArray[menuIndex].order();
				}
				permissionCode = method.getDeclaringClass().getName().substring(baseIndex) + '.' + method.getName() + '-' + menuIndex;
			} else {
				permissionCode = "unknown"; // 如果没有匹配的菜单，则返回"unknown"
			}
		} else {
			Menu currentMenu = method.getAnnotation(Menu.class);
			if (currentMenu != null) {
				setTitle(ServletActionContext.getRequest(), currentMenu);
			}
			permissionCode = method.getDeclaringClass().getName().substring(baseIndex) + '.' + method.getName();
		}
		return permissionCode;
	}

	/**
	 * 设置标题
	 * 
	 * @param request
	 * @param currentMenu
	 */
	protected static final void setTitle(HttpServletRequest request, Menu currentMenu) {
		request.setAttribute("__title", currentMenu.name());
	}
}
