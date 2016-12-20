package me.codeplayer.auth;

import java.lang.reflect.Method;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import me.codeplayer.annotation.Permission;

/**
 * 权限拦截器，用于检测当前用户是否有权限访问当前的类或方法
 * 
 * @author Ready
 * @date 2014-10-19
 */
public class PermissionInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = 1L;
	/** 权限定位符数组在request中的KEY值 */
	public static final String PERMISSION_LOCATOR_KEY = "permissionLocator";
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

	@Inject
	public void setPermissionPolicy(PermissionPolicy permissionPolicy) {
		this.permissionPolicy = permissionPolicy;
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		if (!readyPermissionEnabled) {
			return invocation.invoke();
		}
		// ServletActionContext.getRequest().getSession().getAttribute(sessionUserKey);
		ActionContext context = ActionContext.getContext();
		UserPermission role = (UserPermission) context.getSession().get(sessionUserKey);
		ActionProxy proxy = invocation.getProxy();
		Object action = proxy.getAction();
		int allow = 0;
		String permissionCode = null;
		Class<?> clazz = action.getClass();
		String methodName = proxy.getMethod();
		if (methodName == null)
			methodName = "execute";
		Method method = clazz.getMethod(methodName);
		String methodCode = clazz.getName() + '.' + method.getName();
		Permission p = method.getAnnotation(Permission.class);
		if (p != null) {
			permissionCode = p.value();
			if (permissionCode.length() == 0) {
				permissionCode = permissionPolicy.getCodeFromMethod(invocation, method);
			}
			allow = checkPermission(role, permissionCode) ? 1 : -1;
		} else if ((p = clazz.getAnnotation(Permission.class)) != null) {
			permissionCode = p.value();
			if (permissionCode.length() == 0) {
				permissionCode = permissionPolicy.getCodeFromClass(invocation, clazz);
			}
			allow = checkPermission(role, permissionCode) ? 1 : -1;
		}
		if (allow < 0) {
			throw new PermissionException("你没有权限进行此操作!");
		}
		context.put(PERMISSION_LOCATOR_KEY, new String[] { methodCode, permissionCode });
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
