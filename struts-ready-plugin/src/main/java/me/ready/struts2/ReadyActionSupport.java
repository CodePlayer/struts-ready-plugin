package me.ready.struts2;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 自定义的ActionSupport类，其它Action均可继承此类以实现某些功能
 * 
 * @package me.ready.struts2
 * @author Ready
 * @date 2014-10-23
 */
@SuppressWarnings("serial")
public class ReadyActionSupport extends ActionSupport {

	/**
	 * 快速进行文件下载
	 * 
	 * @param inputStream
	 * @param fileName
	 * @return
	 */
	protected String _download(InputStream inputStream, String fileName) {
		ActionContext ctx = ActionContext.getContext();
		ctx.put("__is", inputStream);
		try {
			ctx.put("__file", new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return "global_download";
	}

	/**
	 * 重定向到指定的Action
	 * 
	 * @param actionName 指定的Action
	 * @return
	 */
	protected String _redirectAction(String actionName) {
		ActionContext ctx = ActionContext.getContext();
		ctx.put("__action", actionName);
		return "global_redirect_action";
	}

	/**
	 * 重定向到指定的URL
	 * 
	 * @param url 指定的URL
	 * @return
	 */
	protected String _redirect(String url) {
		ActionContext ctx = ActionContext.getContext();
		ctx.put("__url", url);
		return "global_redirect";
	}

	/**
	 * 永久重定向到指定的URL
	 * 
	 * @param url 指定的URL
	 * @return
	 */
	protected String _permanentRedirect(String url) {
		ActionContext ctx = ActionContext.getContext();
		ctx.put("__url", url);
		return "global_predirect";
	}
}
