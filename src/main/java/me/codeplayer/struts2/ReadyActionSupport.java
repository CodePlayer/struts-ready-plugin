package me.codeplayer.struts2;

import java.io.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 自定义的ActionSupport类，其它Action均可继承此类以实现某些功能
 * 
 * @author Ready
 * @date 2014-10-23
 */
@SuppressWarnings("serial")
public class ReadyActionSupport extends ActionSupport implements ServletRequestAware {

	private final ReadyAction delegate = new ReadyAction();
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
		delegate.setTitle(title);
	}

	/**
	 * 获取指定参数名称int形式参数值
	 * 
	 * @param name 参数名称
	 * @return
	 * @throws NumberFormatException 如果参数为空或无效
	 */
	protected int getInt(final String name) throws NumberFormatException {
		return delegate.getInt(name);
	}

	/**
	 * 获取指定参数名称int形式参数值
	 * 
	 * @param name 参数名称
	 * @param defaultValue 如果参数值无效，则返回该值
	 * @return
	 */
	protected int getInt(final String name, final int defaultValue) {
		return delegate.getInt(name, defaultValue);
	}

	/**
	 * 获取指定参数名称 integer 形式参数值
	 * 
	 * @param name 参数名称
	 * @param defaultValue 如果参数值无效，则返回该值
	 * @return
	 */
	protected Integer getInteger(String name, Integer defaultValue) {
		return delegate.getInteger(name, defaultValue);
	}

	/**
	 * 快速进行文件下载
	 * 
	 * @param inputStream 指定用于下载的文件输入流
	 * @param downloadFileName 指定响应到客户浏览器的下载文件名称
	 * @return result name
	 */
	protected String _download(InputStream inputStream, String downloadFileName) {
		return delegate._download(inputStream, downloadFileName);
	}

	/**
	 * 快速进行文件下载
	 * 
	 * @param file 用于下载的文件
	 * @param downloadFileName 指定响应到客户浏览器的下载文件名称
	 * @return result name
	 * @throws IllegalArgumentException see {@link FileNotFoundException}
	 */
	protected String _download(File file, String downloadFileName) throws IllegalArgumentException {
		return delegate._download(file, downloadFileName);
	}

	/**
	 * 快速进行文件下载
	 * 
	 * @param filepath 文件所在路径
	 * @param downloadFileName 指定响应到客户浏览器的下载文件名称
	 * @return result name
	 * @throws IllegalArgumentException {@link FileNotFoundException}
	 */
	protected String _download(String filepath, String downloadFileName) throws IllegalArgumentException {
		return delegate._download(filepath, downloadFileName);
	}

	/**
	 * 重定向到指定的Action
	 * 
	 * @param actionName 指定的Action
	 * @return result name
	 */
	protected String _redirectAction(String actionName) {
		return delegate._redirectAction(actionName);
	}

	/**
	 * 重定向到指定的URL
	 * 
	 * @param url 指定的URL
	 * @param permanent 是否是永久重定向(响应状态码：301)
	 * @return result name
	 */
	protected String _redirect(String url, boolean permanent) {
		return delegate._redirect(url, permanent);
	}

	/**
	 * 临时重定向到指定的URL
	 * 
	 * @param url 指定的URL
	 * @return result name
	 */
	protected String _redirect(String url) {
		return delegate._redirect(url, false);
	}

	/**
	 * 将指定文本内容写入响应流中
	 * 
	 * @param text 需要写入的文本内容
	 * @param encoding 指定的字符集编码。如果为null，则默认为"UTF-8"
	 */
	protected void writeToResponse(String text, String encoding) {
		delegate.writeToResponse(text, encoding);
	}

	/**
	 * 将指定文本内容写入响应流中，并设置字符集编码为UTF-8
	 * 
	 * @param text 需要写入的文本内容
	 */
	protected void writeToResponse(String text) {
		delegate.writeToResponse(text, null);
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		delegate.setServletRequest(request);
	}
}
