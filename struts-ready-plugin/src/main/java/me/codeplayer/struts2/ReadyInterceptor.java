package me.codeplayer.struts2;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletResponse;

import me.codeplayer.annotation.Ready;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * struts-ready-plugin的核心拦截器，action的对应方法必须具有@Ready注解
 * 
 * @package me.codeplayer.struts2
 * @author Ready
 * @date 2014-10-25
 */
public class ReadyInterceptor implements Interceptor {

	private static final long serialVersionUID = 1L;

	public void destroy() {}

	public void init() {}

	public String intercept(ActionInvocation invocation) throws Exception {
		ActionProxy proxy = invocation.getProxy();
		Object action = proxy.getAction();
		String methodName = proxy.getMethod();
		boolean notFound = false;
		try {
			Method method = action.getClass().getMethod(methodName);
			notFound = method == null || method.getAnnotation(Ready.class) == null;
		} catch (NoSuchMethodException e) {
			notFound = true;
		}
		if (notFound) {
			ServletActionContext.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND, "无法访问指定的方法!");
			return null;
		}
		return invocation.invoke();
	}
}
