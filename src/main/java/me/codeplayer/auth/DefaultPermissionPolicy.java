package me.codeplayer.auth;

import java.lang.reflect.AnnotatedElement;
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

	@Override
	public PermissionLocator handlePermission(ActionInvocation invocation, final Class<?> clazz, final Method method) {
		Permission p = method.getAnnotation(Permission.class);
		if (p != null) {
			return locatePermission(clazz, method, invocation, method, p);
		} else if ((p = clazz.getAnnotation(Permission.class)) != null) {
			return locatePermission(clazz, method, invocation, clazz, p);
		}
		return null;
	}

	protected PermissionLocator locatePermission(final Class<?> clazz, final Method method, final ActionInvocation invocation, final AnnotatedElement element, final Permission p) {
		String code = p.value();
		Menu[] menus = p.menus();
		PermissionLocator locator = null;
		if (code.length() == 0 || menus.length > 0) {
			if (element instanceof Class) {
				locator = handleMenusBasedClass(clazz, method, invocation, p, menus);
			} else {
				locator = handleMenusBasedMethod(clazz, method, invocation, p, menus);
			}
			if (locator != null && locator.permissionCode == null) {
				throw new PermissionException('[' + element.toString() + "]权限码参数配置有误");
			}
		} else {
			locator = new PermissionLocator(method, 0, methodCodeBuilder(clazz, method).toString(), code);
		}
		return locator;
	}

	protected PermissionLocator handleMenusBasedClass(final Class<?> clazz, final Method method, final ActionInvocation invocation, final Permission p, final Menu[] menus) {
		final String permissionCode = clazz.getName().substring(baseIndex);
		return new PermissionLocator(method, 0, permissionCode, permissionCode);
	}

	protected PermissionLocator handleMenusBasedMethod(final Class<?> clazz, final Method method, final ActionInvocation invocation, final Permission p, final Menu[] menus) {
		final PermissionLocator locator = new PermissionLocator(method, 0, null, null);
		if (menus.length == 0) {
			locator.methodCode = locator.permissionCode = methodCodeBuilder(clazz, method).toString();
		} else if (menus.length > 1) {
			// 如果有@Menus注解，并且有多个@Menu注解
			// 则该方法对应多个菜单、多个权限码：权限码=方法的默认权限码 + 数字后缀(索引或suffix参数值)
			// 后缀为 0 时，不追加后缀
			final HttpServletRequest request = (HttpServletRequest) invocation.getInvocationContext().get(StrutsStatics.HTTP_REQUEST);
			int menuIndex = -1;
			locator.menuSuffix = -1; // 后缀值（由于方法封装的缘故，为了便于该值的引用修改，因此以数组形式表示）
			for (int i = 0; i < menus.length; i++) {
				if (menus[i].suffix() > Menu.DEFAULT_SUFFIX) {
					locator.menuSuffix = menus[i].suffix();
				} else {
					locator.menuSuffix++;
				}
				if (matchMethodMenu(request, menus[i], locator)) {
					menuIndex = i;
					break;
				}
			}
			if (menuIndex != -1) {
				setTitle(request, menus[menuIndex]);
				if (locator.menuSuffix > 0) {
					locator.methodCode = methodCodeBuilder(clazz, method).append('-').append(locator.menuSuffix).toString();
				}
				locator.permissionCode = buildMethodPermissionCode(
						methodPermissionBuilder(clazz, method, invocation, p, menus),
						menus[menuIndex],
						locator.menuSuffix);
			}
		}
		if (locator.methodCode == null) {
			locator.methodCode = methodCodeBuilder(clazz, method).toString();
		}
		return locator;
	}

	protected boolean matchMethodMenu(final HttpServletRequest request, final Menu menu, final PermissionLocator locator) {
		final String[] args = menu.args(); // 菜单所需校验的额外请求参数的键值对数组
		if (args.length > 0) {
			if ((args.length & 1) != 0) { // 数组长度必须为偶数
				throw new PermissionException("The number of arguments passed in args() must be even!");
			}
			for (int j = 0; j < args.length; j++) {
				String value = request.getParameter(args[j++]);
				if (value == null || value.length() == 0) { // 没有参数值，直接返回 false
					return false;
				}
				String expected = args[j]; // 期望值
				if ("*".equals(expected) || value.equals(expected)) { // 如果 args()的参数值为"*"，则不校验具体的值
					continue;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	protected StringBuilder methodPermissionBuilder(final Class<?> clazz, final Method method, final ActionInvocation invocation, final Permission p, final Menu[] menus) {
		String code = p.value();
		return code.length() > 0 ? new StringBuilder(code)
				: methodCodeBuilder(clazz, method);
	}

	protected StringBuilder methodCodeBuilder(Class<?> clazz, Method method) {
		return new StringBuilder().append(clazz.getName().substring(baseIndex)).append('.').append(method.getName());
	}

	protected String buildMethodPermissionCode(final StringBuilder permissionCodeBuilder, final Menu menu, final int suffix) {
		if (suffix > 0) { // 如果大于0才添加后缀
			permissionCodeBuilder.append('-').append(suffix);
		}
		return permissionCodeBuilder.toString();
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
