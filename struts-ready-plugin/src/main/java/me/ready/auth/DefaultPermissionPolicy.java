package me.ready.auth;

import java.lang.reflect.Method;

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
		return method.getDeclaringClass().getName().substring(baseIndex) + '.' + method.getName();
	}
}
