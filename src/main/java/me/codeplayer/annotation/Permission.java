package me.codeplayer.annotation;

import java.lang.annotation.*;

/**
 * 用于进行权限控制的注解，将该注解应用在对应的类型或方法上，即可在调用指定的类型或方法时，自动进行对应的权限判断<br>
 * 该注解只对作为Struts2的Action的类或方法生效
 * 
 * @author Ready
 * @date 2014-10-19
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permission {

	/**
	 * 权限码
	 */
	String value() default "";

	/**
	 * 需要进行多权限管理的方法菜单数组<br>
	 * 该注解属性只能在方法上定义，否则无效
	 */
	Menu[] menus() default {};
}
