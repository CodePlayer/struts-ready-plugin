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
		Menus menus = method.getAnnotation(Menus.class);
		if (menus != null && menus.value().length > 1) {
			HttpServletRequest request = ServletActionContext.getRequest();
			Menu[] menuArray = menus.value();
			int menuIndex = -1;
			for (int i = 0; i < menuArray.length; i++) {
				Menu menu = menuArray[i];
				String[] args = menu.args();
				if ((args.length & 1) != 0) {
					throw new IllegalArgumentException("The number of arguments passed in args() must be even!");
				}
				if (args.length > 0) {
					boolean isCurrent = true;
					for (int j = 0; j < args.length; j++) {
						String value = request.getParameter(args[j++]);
						if (StringUtil.isEmpty(value)) {
							isCurrent = false;
							break;
						}
						String expected = args[j];
						if ("*".equals(expected) || value.equals(expected)) {
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
				request.setAttribute("__title", menuArray[menuIndex].name());
				return method.getDeclaringClass().getName().substring(baseIndex) + '.' + method.getName() + '-' + menuIndex;
			} else {
				return "unknown";
			}
		} else {
			Menu menu = method.getAnnotation(Menu.class);
			if (menu != null) {
				HttpServletRequest request = ServletActionContext.getRequest();
				request.setAttribute("__title", menu.name());
			}
			return method.getDeclaringClass().getName().substring(baseIndex) + '.' + method.getName();
		}
	}
}
