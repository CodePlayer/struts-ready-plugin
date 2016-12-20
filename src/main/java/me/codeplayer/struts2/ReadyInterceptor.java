package me.codeplayer.struts2;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.Interceptor;

import me.codeplayer.annotation.Ready;

/**
 * struts-ready-plugin的核心拦截器，action的对应方法必须具有@Ready注解
 * 
 * @author Ready
 * @date 2014-10-25
 */
public class ReadyInterceptor implements Interceptor {

	private static final long serialVersionUID = 1L;

	public void destroy() {
	}

	public void init() {
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		ActionProxy proxy = invocation.getProxy();
		Object action = proxy.getAction();
		String methodName = proxy.getMethod();
		boolean notFound = false;
		try {
			Method method = action.getClass().getMethod(methodName);
			Ready ready = null;
			notFound = method == null || (ready = method.getAnnotation(Ready.class)) == null;
			if (ready != null && ready.value().length() > 0) { // 设置标题
				HttpServletRequest request = (HttpServletRequest) invocation.getInvocationContext().get(StrutsStatics.HTTP_REQUEST);
				request.setAttribute(Ready.TITLE_KEY, ready.value());
			}
		} catch (NoSuchMethodException e) {
			notFound = true;
		}
		if (notFound) {
			HttpServletResponse response = (HttpServletResponse) invocation.getInvocationContext().get(StrutsStatics.HTTP_RESPONSE);
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource Not Found");
			return null;
		}
		return invocation.invoke();
	}
}
