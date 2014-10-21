package me.ready.auth;

import java.lang.reflect.Method;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * 权限拦截器，用于检测当前用户是否有权限访问当前的类或方法
 * 
 * @package me.ready.auth
 * @author Ready
 * @date 2014-10-19
 */
public class PermissionInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = -8700565515654350711L;
	/** 存储用户信息的session key */
	@Inject("struts.ready.sessionUserKey")
	protected String sessionUserKey; // 存储用户信息的session key
	/** 是否启用权限控制 */
	@Inject("struts.enable.ready.permission")
	protected boolean readyPermissionEnabled;

	public String intercept(ActionInvocation invocation) throws Exception {
		if (!readyPermissionEnabled) {
			return invocation.invoke();
		}
		// ServletActionContext.getRequest().getSession().getAttribute(sessionUserKey);
		UserPermission role = (UserPermission) ActionContext.getContext().getSession().get(sessionUserKey);
		ActionProxy proxy = invocation.getProxy();
		Object action = proxy.getAction();
		int allow = 0;
		boolean hasAnnotation = action.getClass().isAnnotationPresent(Permission.class);
		if (hasAnnotation) {
			Permission p = action.getClass().getAnnotation(Permission.class);
			allow = isAllowAccess(role, p.value(), proxy.getActionName()) ? 1 : -1;
		}
		if (allow <= 0 && (!hasAnnotation || role != null)) {
			String methodName = proxy.getMethod();
			if (methodName == null) methodName = "execute";
			try {
				Method method = action.getClass().getMethod(methodName);
				if (method.isAnnotationPresent(Permission.class)) {
					Permission p = method.getAnnotation(Permission.class);
					allow = isAllowAccess(role, p.value(), methodName) ? 1 : -1;
				}
			} catch (NoSuchMethodException e) {
				// ignore exception
			}
		}
		if (allow < 0) {
			throw new PermissionException("你没有权限进行此操作!");
		}
		return invocation.invoke();
	}

	/**
	 * 判断指定角色是否允许访问指定的权限码
	 * 
	 * @param role 用户角色
	 * @param code 注解指定的权限码
	 * @param defaultValue 默认的权限码
	 * @return
	 */
	protected boolean isAllowAccess(UserPermission role, String code, String defaultValue) {
		if (role == null) return false;
		if (code.length() == 0) code = defaultValue;
		return role.hasPermission(code);
	}
}
