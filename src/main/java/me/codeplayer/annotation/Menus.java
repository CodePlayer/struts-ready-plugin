package me.codeplayer.annotation;

import java.lang.annotation.*;

/**
 * 菜单注解
 * 
 * @author Ready
 * @date 2015年2月2日
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Menus {

	Menu[] value() default {};
}
