package me.codeplayer.struts2;

import org.apache.commons.lang3.*;
import org.apache.struts2.factory.*;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.*;
import com.opensymphony.xwork2.config.entities.*;
import com.opensymphony.xwork2.util.profiling.*;

/**
 * 由于 StrutsActionProxy.prepare() 存在 allowedMethods 的方法名硬性限制，因此需要继承该类并重写该方法，以移除限制
 * 
 * @author Ready
 *
 */
public class ReadyStrutsActionProxy extends StrutsActionProxy {

	private static final long serialVersionUID = 1L;

	private boolean methodSpecified = true;

	public ReadyStrutsActionProxy(ActionInvocation inv, String namespace, String actionName, String methodName, boolean executeResult, boolean cleanupContext) {
		super(inv, namespace, actionName, methodName, executeResult, cleanupContext);
	}

	private void resolveMethod() {
		// if the method is set to null, use the one from the configuration
		// if the one from the configuration is also null, use "execute"
		if (StringUtils.isEmpty(this.method)) {
			this.method = config.getMethodName();
			if (StringUtils.isEmpty(this.method)) {
				this.method = ActionConfig.DEFAULT_METHOD;
			}
			methodSpecified = false;
		}
	}

	@SuppressWarnings({ "deprecation" })
	@Override
	protected void prepare() {
		String profileKey = "create DefaultActionProxy: ";
		try {
			UtilTimerStack.push(profileKey);
			config = configuration.getRuntimeConfiguration().getActionConfig(namespace, actionName);

			if (config == null && unknownHandlerManager.hasUnknownHandlers()) {
				config = unknownHandlerManager.handleUnknownAction(namespace, actionName);
			}
			if (config == null) {
				throw new ConfigurationException(getErrorMessage());
			}

			resolveMethod();
			invocation.init(this);
			/*
			if (config.isAllowedMethod(method)) {
				invocation.init(this);
			} else {
				throw new ConfigurationException(prepareNotAllowedErrorMessage());
			}
			*/
		} finally {
			UtilTimerStack.pop(profileKey);
		}
	}

	@Override
	public boolean isMethodSpecified() {
		return methodSpecified;
	}

}
