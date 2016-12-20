package me.codeplayer.auth;

import java.lang.reflect.AnnotatedElement;

import com.opensymphony.xwork2.ActionInvocation;

import me.codeplayer.annotation.Menu;
import me.codeplayer.annotation.Permission;

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
	 * @param element 标记了 Permission 注解的类（Class）或方法（Method）
	 * @param actionInvocation
	 * @param permission
	 * @param menus 即 <code>permission.menus()</code>的返回值， 由于注解返回数组的方法，每次调用均返回新的数组，为了避免不必要的开销，特传入该参数
	 * @return 权限码字符串。如果返回null，则表示配置有误，权限拦截器将抛出异常
	 */
	String getPermissionCode(AnnotatedElement element, ActionInvocation actionInvocation, Permission permission, Menu[] menus);
}
