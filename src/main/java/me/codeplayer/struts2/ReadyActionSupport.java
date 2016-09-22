package me.codeplayer.struts2;

import java.io.*;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 自定义的ActionSupport类，其它Action均可继承此类以实现某些功能
 * 
 * @author Ready
 * @date 2014-10-23
 */
@SuppressWarnings("serial")
public class ReadyActionSupport extends ActionSupport {

	/**
	 * 快速进行文件下载
	 * 
	 * @param inputStream 指定的用于下载的文件输入流
	 * @param downloadFileName 指定响应到客户浏览器的下载文件名称
	 * @return
	 */
	protected String _download(InputStream inputStream, String downloadFileName) {
		ActionContext ctx = ActionContext.getContext();
		ctx.put("__is", inputStream);
		ctx.put("__file", new String(downloadFileName.getBytes(Charset.forName("UTF-8")), Charset.forName("ISO-8859-1")));
		return "global_download";
	}

	/**
	 * 快速进行文件下载
	 * 
	 * @param inputStream 指定的用于下载的文件输入流
	 * @param downloadFileName 指定响应到客户浏览器的下载文件名称
	 * @return
	 */
	protected String _download(File file, String downloadFileName) {
		try {
			return _download(new FileInputStream(file), downloadFileName);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * 快速进行文件下载
	 * 
	 * @param inputStream 指定的用于下载的文件输入流
	 * @param downloadFileName 指定响应到客户浏览器的下载文件名称
	 * @return
	 */
	protected String _download(String filepath, String downloadFileName) {
		return _download(new File(filepath), downloadFileName);
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

	/**
	 * 将指定文本内容写入响应流中
	 * 
	 * @param text 需要写入的文本内容
	 * @param encoding 指定的字符集编码。如果为null，则默认为"UTF-8"
	 */
	protected void writeToResponse(String text, String encoding) {
		ActionContext.getContext().getActionInvocation().getProxy().setExecuteResult(false);
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType(encoding == null ? "text/html;charset=UTF-8" : "text/html;charset=" + encoding);
		try {
			response.getWriter().write(text);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * 将指定文本内容写入响应流中，并设置字符集编码为UTF-8
	 * 
	 * @param text 需要写入的文本内容
	 */
	protected void writeToResponse(String text) {
		writeToResponse(text, null);
	}
}
