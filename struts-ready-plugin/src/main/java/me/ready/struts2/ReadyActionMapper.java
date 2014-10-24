package me.ready.struts2;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.RequestUtils;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * 自定义的ActionMapper接口实现类
 * 
 * @package me.ready.struts2
 * @author Ready
 * @date 2014-10-19
 */
public class ReadyActionMapper extends DefaultActionMapper {

	private static final Logger LOG = LoggerFactory.getLogger(ReadyActionMapper.class);
	/** 是否启用struts-ready-plugin插件 */
	protected boolean readyEnabled;
	/** 默认的action名称 */
	protected String defaultAction;
	/** 默认的method名称 */
	protected String defaultMethod;
	/** 是否启用默认的action和名称 */
	protected boolean defaultEnabled;

	@Inject("struts.ready.enable")
	public void setReadyEnabled(String enabled) {
		this.readyEnabled = "true".equalsIgnoreCase(enabled);
		if (this.readyEnabled) {
			LOG.debug("已启用struts-ready-plugin");
		}
	}

	@Inject("struts.ready.default.action")
	public void setDefaultAction(String defaultAction) {
		LOG.debug("默认的action为[#0]", defaultAction);
		this.defaultAction = defaultAction;
	}

	@Inject("struts.ready.default.method")
	public void setDefaultMethod(String defaultMethod) {
		LOG.debug("默认的method为[#0]", defaultMethod);
		this.defaultMethod = defaultMethod;
	}

	@Inject("struts.ready.enable.default")
	public void setDefaultEnabled(String enabled) {
		this.defaultEnabled = "true".equalsIgnoreCase(enabled);
	}

	@Override
	public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
		if (!readyEnabled) {
			return super.getMapping(request, configManager);
		}
		ActionMapping mapping = new ActionMapping();
		String uri = RequestUtils.getUri(request);
		// 如果URI后面中有分号，可能是jsessionid，需要过滤掉再判断
		int indexOfSemicolon = uri.indexOf(';');
		if (indexOfSemicolon > -1) {
			uri = uri.substring(0, indexOfSemicolon);
		}
		uri = dropExtension(uri, mapping);
		if (uri == null) {
			return null;
		}
		parseNameAndNamespaceAndMethod(uri, mapping, configManager);
		System.out.println("解析数据：namespace[" + mapping.getNamespace() + "], action[" + mapping.getName() + "], method[" + mapping.getMethod() + "]");
		if (LOG.isTraceEnabled()) {
			LOG.trace("解析数据：namespace[" + mapping.getNamespace() + "], action[" + mapping.getName() + "], method[" + mapping.getMethod() + "]");
		}
		handleSpecialParameters(request, mapping);
		if (mapping.getName() == null) {
			return null;
		}
		return mapping;
	}

	/**
	 * 从URI中解析action的名称、命名空间以及方法名称
	 * 
	 * @param uri 指定的URI
	 * @param mapping 用于组装的ActionMapping
	 */
	protected void parseNameAndNamespaceAndMethod(String uri, ActionMapping mapping, ConfigurationManager configManager) {
		String namespace = null, name = null, method = null;
		int lastSlash = uri.lastIndexOf('/');
		if (defaultEnabled && mapping.getExtension() == null && lastSlash + 1 == uri.length()) {
			uri = uri.substring(0, lastSlash);
			Configuration config = configManager.getConfiguration();
			Collection<PackageConfig> packages = config.getPackageConfigs().values();
			PackageConfig pack = getPackageByNamespace(packages, uri);
			if (pack != null && pack.getActionConfigs().get(defaultAction) != null) {
				mapping.setNamespace(uri);
				mapping.setName(defaultAction);
				mapping.setMethod(defaultMethod);
				return;
			}
			lastSlash = uri.lastIndexOf('/');
			if (lastSlash != -1) {
				namespace = uri.substring(0, lastSlash);
				if (getPackageByNamespace(packages, namespace) != null) {
					mapping.setName(uri.substring(lastSlash + 1));
					mapping.setNamespace(namespace);
					mapping.setMethod(defaultMethod);
				}
			}
		} else if (lastSlash > 0) {
			if (method == null) {
				method = uri.substring(lastSlash + 1);
				uri = uri.substring(0, lastSlash);
			}
			lastSlash = uri.lastIndexOf('/');
			if (lastSlash == -1) {
				namespace = "";
				name = uri;
			} else if (lastSlash == 0) {
				namespace = "/";
				name = uri.substring(lastSlash + 1);
			} else {
				namespace = uri.substring(0, lastSlash);
				name = uri.substring(lastSlash + 1);
			}
			mapping.setNamespace(namespace);
			mapping.setName(name);
			mapping.setMethod(method);
		}
	}

	@Override
	public String getUriFromActionMapping(ActionMapping mapping) {
		if (!readyEnabled) {
			return super.getUriFromActionMapping(mapping);
		}
		StringBuilder uri = new StringBuilder();
		String value = mapping.getNamespace();
		if (value != null) {
			uri.append(value);
			if (!"/".equals(value)) {
				uri.append('/');
			}
		}
		uri.append(mapping.getName()).append('/').append(mapping.getMethod());
		handleExtension(mapping, uri);
		handleParams(mapping, uri);
		return uri.toString();
	}

	/**
	 * 根据指定的命名空间来获取包配置信息
	 * 
	 * @param packages 指定包配置集合
	 * @param namespace 指定的命名空间
	 * @return
	 */
	public static final PackageConfig getPackageByNamespace(Collection<PackageConfig> packages, String namespace) {
		for (PackageConfig cfg : packages) {
			if (namespace.equals(cfg.getNamespace())) {
				return cfg;
			}
		}
		return null;
	}
}
