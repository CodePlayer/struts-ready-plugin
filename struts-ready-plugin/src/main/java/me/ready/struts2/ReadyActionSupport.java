package me.ready.struts2;

import java.io.InputStream;
import java.nio.charset.Charset;

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
	 * @param inputStream 指定的用于下载的文件输入流
	 * @param fileName 指定响应到客户浏览器的下载文件名称
	 * @return
	 */
	protected String _download(InputStream inputStream, String fileName) {
		ActionContext ctx = ActionContext.getContext();
		ctx.put("__is", inputStream);
		ctx.put("__file", new String(fileName.getBytes(Charset.forName("UTF-8")), Charset.forName("ISO-8859-1")));
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
	 * @param permanent 是否是永久重定向(响应状态码：301)
	 * @return
	 */
	protected String _redirect(String url, boolean permanent) {
		ActionContext ctx = ActionContext.getContext();
		ctx.put("__url", url);
		return permanent ? "global_predirect" : "global_redirect";
	}

	/**
	 * 临时重定向到指定的URL
	 * 
	 * @param url 指定的URL
	 * @return
	 */
	protected String _redirect(String url) {
		return _redirect(url, false);
	}
}
