package me.codeplayer.struts2;

import com.opensymphony.xwork2.*;

/**
 * 需要本 Factory 与 {@link ReadyStrutsActionProxy} 配套
 * 
 * @author Ready
 *
 */
public class ReadyStrutsActionProxyFactory extends DefaultActionProxyFactory {

	@Override
	public ActionProxy createActionProxy(ActionInvocation inv, String namespace, String actionName, String methodName, boolean executeResult, boolean cleanupContext) {
		ReadyStrutsActionProxy proxy = new ReadyStrutsActionProxy(inv, namespace, actionName, methodName, executeResult, cleanupContext);
		container.inject(proxy);
		proxy.prepare();
		return proxy;
	}
}