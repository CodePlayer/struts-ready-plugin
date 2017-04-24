package me.codeplayer.struts2;

import java.io.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

import me.codeplayer.annotation.Ready;

/**
 * 自定义的Action类，其它Action均可继承此类以实现某些通用功能
 * 
 * @author Ready
 * @date 2014-10-23
 */
public class ReadyAction implements Action, ServletRequestAware {

	/**
	 * HttpServletRequest对象，已经过Struts注入，可直接使用
	 */
	protected HttpServletRequest request;

	/**
	 * 设置页面的标题
	 *
	 * @param title 标题
	 */
	protected void setTitle(String title) {
		request.setAttribute(Ready.TITLE_KEY, title);
	}

	/**
	 * 获取指定参数名称int形式参数值
	 * 
	 * @param name 参数名称
	 * @return
	 * @throws NumberFormatException 如果参数为空或无效
	 */
	protected int getInt(final String name) throws NumberFormatException {
		final String val = request.getParameter(name);
		if (StringUtils.isEmpty(val)) {
			throw new NumberFormatException();
		}
		return Integer.parseInt(val);
	}

	/**
	 * 获取指定参数名称int形式参数值
	 * 
	 * @param name 参数名称
	 * @param defaultValue 如果参数值无效，则返回该值
	 * @return
	 */
	protected int getInt(final String name, final int defaultValue) {
		final String val = request.getParameter(name);
		if (StringUtils.isEmpty(val)) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(val);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 获取指定参数名称 integer 形式参数值
	 * 
	 * @param name 参数名称
	 * @param defaultValue 如果参数值无效，则返回该值
	 * @return
	 */
	protected Integer getInteger(String name, Integer defaultValue) {
		final String val = request.getParameter(name);
		if (StringUtils.isEmpty(val)) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(val);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 快速进行文件下载
	 * 
	 * @param inputStream 指定的用于下载的文件输入流
	 * @param downloadFileName 指定响应到客户浏览器的下载文件名称
	 * @return result name
	 */
	protected String _download(InputStream inputStream, String downloadFileName) {
		request.setAttribute("__is", inputStream);
		try {
			request.setAttribute("__file", new String(downloadFileName.getBytes("UTF-8"), "ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
		return "global_download";
	}

	/**
	 * 快速进行文件下载
	 * 
	 * @param inputStream 指定的用于下载的文件输入流
	 * @param downloadFileName 指定响应到客户浏览器的下载文件名称
	 * @return result name
	 * @throws IllegalArgumentException see {@link FileNotFoundException}
	 */
	protected String _download(File file, String downloadFileName) throws IllegalArgumentException {
		try {
			return _download(new FileInputStream(file), downloadFileName);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * 快速进行文件下载
	 * 
	 * @param filepath 文件所在路径
	 * @param downloadFileName 指定响应到客户浏览器的下载文件名称
	 * @return result name
	 * @throws IllegalArgumentException see {@link FileNotFoundException}
	 */
	protected String _download(String filepath, String downloadFileName) throws IllegalArgumentException {
		return _download(new File(filepath), downloadFileName);
	}

	/**
	 * 重定向到指定的Action
	 * 
	 * @param actionName 指定的Action
	 * @return result name
	 */
	protected String _redirectAction(String actionName) {
		ActionContext.getContext().put("__action", actionName);
		return "global_redirect_action";
	}

	/**
	 * 重定向到指定的URL
	 * 
	 * @param url 指定的URL
	 * @param permanent 是否是永久重定向(响应状态码：301)
	 * @return result name
	 */
	protected String _redirect(String url, boolean permanent) {
		ActionContext.getContext().put("__url", url);
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
		final ActionContext context = ActionContext.getContext();
		final HttpServletResponse response = (HttpServletResponse) context.get(StrutsStatics.HTTP_RESPONSE);
		response.setContentType(encoding == null ? "text/html;charset=UTF-8" : "text/html;charset=" + encoding);
		try {
			response.getWriter().write(text);
			context.getActionInvocation().getProxy().setExecuteResult(false);
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

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String execute() throws Exception {
		return SUCCESS;
	}
}
