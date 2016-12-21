package me.codeplayer.auth;

import java.lang.reflect.Method;

/**
 * 权限定位符
 * 
 * @author Ready
 * @date 2016年12月20日
 */
public class PermissionLocator {

	protected Method method;
	protected String methodCode;
	protected String permissionCode;

	public PermissionLocator() {
	}

	public PermissionLocator(Method method, String methodCode, String permissionCode) {
		this.method = method;
		this.methodCode = methodCode;
		this.permissionCode = permissionCode;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getMethodCode() {
		return methodCode;
	}

	public void setMethodCode(String methodCode) {
		this.methodCode = methodCode;
	}

	public String getPermissionCode() {
		return permissionCode;
	}

	public void setPermissionCode(String permissionCode) {
		this.permissionCode = permissionCode;
	}
}
