package me.codeplayer.auth;

import java.lang.reflect.Method;

import com.opensymphony.xwork2.ActionInvocation;

/**
 * 权限码生成策略接口
 * 
 * @author Ready
 * @date 2015年2月2日
 */
public interface PermissionPolicy {

	/**
	 * 根据指定的参数生成对应的权限码
	 * 
	 * @param method 当前请求对应的方法
	 * @param actionInvocation
	 * @param permission
	 * @param menus 即 <code>permission.menus()</code>的返回值， 由于注解返回数组的方法，每次调用均返回新的数组，为了避免不必要的开销，特传入该参数
	 * @return 权限定位符对象。如果返回null，则表示无需权限控制
	 */
	PermissionLocator handlePermission(ActionInvocation actionInvocation, Class<?> clazz, Method method);
}
