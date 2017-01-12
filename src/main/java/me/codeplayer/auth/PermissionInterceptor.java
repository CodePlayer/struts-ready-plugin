package me.codeplayer.auth;

import java.lang.reflect.Method;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import me.codeplayer.struts2.ReadyInterceptor;

/**
 * 权限拦截器，用于检测当前用户是否有权限访问当前的类或方法
 * 
 * @author Ready
 * @date 2014-10-19
 */
public class PermissionInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = 1L;
	//
	/** 存储用户信息的session key */
	protected String sessionUserKey; // 存储用户信息的session key
	/** 是否启用权限控制 */
	protected boolean readyPermissionEnabled;
	protected PermissionPolicy permissionPolicy;

	@Inject("struts.ready.sessionUserKey")
	public void setSessionUserKey(String sessionUserKey) {
		this.sessionUserKey = sessionUserKey;
	}

	@Inject("struts.ready.enable.permission")
	public void setReadyPermissionEnabled(String enabled) {
		this.readyPermissionEnabled = Boolean.parseBoolean(enabled);
	}

	@Inject("ready")
	public void setPermissionPolicy(PermissionPolicy permissionPolicy) {
		this.permissionPolicy = permissionPolicy;
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		if (!readyPermissionEnabled) {
			return invocation.invoke();
		}
		ActionContext context = ActionContext.getContext();
		UserPermission role = (UserPermission) context.getSession().get(sessionUserKey);
		ActionProxy proxy = invocation.getProxy();
		final Class<?> clazz = proxy.getAction().getClass();
		final Method method = ReadyInterceptor.getTargetMethod(invocation);
		final PermissionLocator locator = permissionPolicy.handlePermission(invocation, clazz, method);
		if (locator != null
				&&
				(role == null || !role.hasPermission(locator.permissionCode))) {
			throw new PermissionException("你没有权限进行此操作!");
		}
		context.put(PermissionLocator.PERMISSION_LOCATOR_KEY, locator);
		return invocation.invoke();
	}

	/**
	 * 判断指定角色是否允许访问指定的权限码
	 * 
	 * @param role 用户角色
	 * @param code 注解指定的权限码
	 * @return
	 */
	protected boolean checkPermission(UserPermission role, String code) {
		if (role == null)
			return false;
		return role.hasPermission(code);
	}
}
