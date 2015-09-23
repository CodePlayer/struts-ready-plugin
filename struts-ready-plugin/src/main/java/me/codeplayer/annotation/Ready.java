package me.codeplayer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解用于标识Action类中的方法准备已经就绪，允许外部访问
 * 
 * @package me.codeplayer.annotation
 * @author Ready
 * @date 2014-11-3
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Ready {
	
}
