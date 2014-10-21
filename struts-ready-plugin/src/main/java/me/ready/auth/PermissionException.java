package me.ready.auth;

/**
 * 项目中的访问权限异常类，用于表示由于没有对应权限所引起的异常
 * 
 * @author Ready
 * @date 2012-4-23
 */
public class PermissionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * 构造具有默认提示信息"你没有权限执行此操作!"的访问权限异常实例
	 */
	public PermissionException() {
		super("你没有权限执行此操作!");
	}

	/**
	 * 构造具有指定异常信息的访问权限异常实例
	 * 
	 * @param message 指定的异常信息
	 */
	public PermissionException(String message) {
		super(message);
	}

	/**
	 * 构造具有指定异常源(导致该异常的异常)的访问权限异常实例
	 * 
	 * @param cause 指定的异常源
	 */
	public PermissionException(Throwable cause) {
		super(cause);
	}

	/**
	 * 构造具有指定异常信息和异常源(导致该异常的异常)的访问权限异常实例
	 * 
	 * @param message 指定的异常信息
	 * @param cause 指定的异常源
	 */
	public PermissionException(String message, Throwable cause) {
		super(message, cause);
	}
}
