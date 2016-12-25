package me.codeplayer.struts2;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import me.codeplayer.annotation.Ready;

/**
 * struts-ready-plugin的核心拦截器，action的对应方法必须具有@Ready注解
 * 
 * @author Ready
 * @date 2014-10-25
 */
public class ReadyInterceptor extends AbstractInterceptor {

	private static final Logger LOG = LoggerFactory.getLogger(ReadyInterceptor.class);
	private static final long serialVersionUID = 1L;

	public String intercept(ActionInvocation invocation) throws Exception {
		final Method method = getTargetMethod(invocation);
		Ready ready = null;
		boolean notFound = method == null || (ready = method.getAnnotation(Ready.class)) == null;
		if (ready != null && ready.value().length() > 0) { // 设置标题
			HttpServletRequest request = (HttpServletRequest) invocation.getInvocationContext().get(StrutsStatics.HTTP_REQUEST);
			request.setAttribute(Ready.TITLE_KEY, ready.value());
		}
		if (notFound) {
			HttpServletResponse response = (HttpServletResponse) invocation.getInvocationContext().get(StrutsStatics.HTTP_RESPONSE);
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource Not Found");
			return null;
		}
		return invocation.invoke();
	}

	/** 用于存储当前请求调用的目标方法（Method对象）的 ActionContext key */
	public static final String TARGET_METHOD_KEY = "struts.interceptor.targetMethod";

	/**
	 * 获取当前请求调用的目标方法（Method对象）<br>
	 * 由于每次通过 <code> Class.getMethod()</code> 方法获取到的 Method 对象都是一个新的实例，因此内部通过 ActionContext 线程缓存的方式来避免重复获取Method对象的开销
	 * 
	 * @param context
	 * @return
	 */
	public static final Method getTargetMethod(ActionInvocation invocation) {
		final ActionContext context = invocation.getInvocationContext();
		Method method = (Method) context.get(TARGET_METHOD_KEY);
		if (method == null) {
			ActionProxy proxy = invocation.getProxy();
			String methodName = proxy.getMethod();
			if (methodName == null)
				methodName = ActionConfig.DEFAULT_METHOD;
			try {
				method = proxy.getAction().getClass().getMethod(methodName);
			} catch (NoSuchMethodException e) {
				LOG.warn("getTargetMethod() failed", e);
			}
			if (method != null) {
				context.put(TARGET_METHOD_KEY, method);
			}
		}
		return method;
	}
}
