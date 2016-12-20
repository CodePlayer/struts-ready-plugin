package me.codeplayer.annotation;

import java.lang.annotation.*;

/**
 * 该注解用于标识Action类中的方法准备已经就绪，允许外部访问
 * 
 * @author Ready
 * @date 2014-11-3
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ready {

	/** 标题在request中的KEY值 */
	String TITLE_KEY = "__title";

	/**
	 * 页面的标题
	 * 
	 * @return
	 */
	String value() default "";

	/**
	 * 额外的信息字符串
	 * 
	 * @return
	 */
	String extra() default "";
}
