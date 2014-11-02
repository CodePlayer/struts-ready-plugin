package me.ready.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于进行权限控制的注解，将该注解应用在对应的类型或方法上，即可在调用指定的类型或方法时，自动进行对应的权限判断<br>
 * 该注解只对作为Struts2的Action的类或方法生效
 * 
 * @package me.ready.annotation
 * @author Ready
 * @date 2014-10-19
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {

	String value() default "";
}
