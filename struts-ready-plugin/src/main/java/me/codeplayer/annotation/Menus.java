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
public @interface Menus {

	Menu[] value() default {};
}
