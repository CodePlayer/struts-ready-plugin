package me.codeplayer.auth;

import java.lang.reflect.Method;

/**
 * 权限定位符
 * 
 * @author Ready
 * @date 2016年12月20日
 */
public class PermissionLocator {

	/** 权限定位符数组在request中的KEY值 */
	public static final String PERMISSION_LOCATOR_KEY = "permissionLocator";
	//
	protected Method method;
	protected int menuSuffix;
	protected String methodCode;
	protected String permissionCode;

	public PermissionLocator() {
	}

	public PermissionLocator(Method method, int menuSuffix, String methodCode, String permissionCode) {
		this.method = method;
		this.menuSuffix = menuSuffix;
		this.methodCode = methodCode;
		this.permissionCode = permissionCode;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public int getMenuSuffix() {
		return menuSuffix;
	}

	public void setMenuSuffix(int menuSuffix) {
		this.menuSuffix = menuSuffix;
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
