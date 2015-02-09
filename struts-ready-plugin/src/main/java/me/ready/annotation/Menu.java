package me.ready.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 菜单注解
 * 
 * @package me.ready.annotation
 * @author Ready
 * @date 2015年2月2日
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Menu {

	int DEFAULT_ORDER = -1;

	/**
	 * 菜单名称
	 */
	String name();

	/**
	 * 菜单参数。例如：<code> {"status", "1", "type", "1"} </code>
	 */
	String[] args() default {};

	/**
	 * 菜单顺序
	 */
	int order() default DEFAULT_ORDER;
}
