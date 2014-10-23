package me.ready.struts2;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.RequestUtils;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;

import com.opensymphony.xwork2.config.ConfigurationManager;
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

	@Inject("struts.ready.enable")
	public void setReadyEnabled(String enabled) {
		this.readyEnabled = "true".equalsIgnoreCase(enabled);
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
	 * Parses the name and namespace from the uri
	 * 
	 * @param uri The uri
	 * @param mapping The action mapping to populate
	 */
	protected void parseNameAndNamespaceAndMethod(String uri, ActionMapping mapping, ConfigurationManager configManager) {
		String namespace = "", name, method;
		int lastSlash = uri.lastIndexOf('/');
		if (lastSlash > 0) {
			method = uri.substring(lastSlash + 1);
			uri = uri.substring(0, lastSlash);
			lastSlash = uri.lastIndexOf('/');
			if (lastSlash == -1) {
				name = uri;
			} else if (lastSlash == 0) {
				namespace = "/";
				name = uri.substring(lastSlash + 1);
			} else {
				namespace = uri.substring(0, lastSlash);
				name = uri.substring(lastSlash + 1);
			}
			mapping.setNamespace(namespace);
			mapping.setName(cleanupActionName(name));
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
}
