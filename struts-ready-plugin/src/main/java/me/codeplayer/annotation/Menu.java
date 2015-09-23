package me.codeplayer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 菜单注解
 * 
 * @package me.codeplayer.annotation
 * @author Ready
 * @date 2015年2月2日
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Menu {

	int DEFAULT_SUFFIX = -1;

	/**
	 * 菜单名称
	 */
	String name();

	/**
	 * 菜单参数。例如：<code> {"status", "1", "type", "1"} </code>
	 */
	String[] args() default {};

	/**
	 * 菜单权限码后缀。<br>
	 * 默认为-1，(以及其他小于0的值)后缀为默认索引，如果设为0则表示不添加后缀
	 */
	int suffix() default DEFAULT_SUFFIX;
}
