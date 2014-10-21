package me.ready.auth;

/**
 * 用户角色接口
 * 
 * @package me.ready.auth
 * @author Ready
 * @date 2014-10-20
 */
public interface UserPermission {

	/**
	 * 判断当前角色是否具备指定权限码所表示的权限
	 * 
	 * @param code 指定的权限码
	 * @return
	 */
	public boolean hasPermission(String code);

	/**
	 * 判断当前角色是否具备访问指定URL的权限
	 * 
	 * @param uri 指定的URI，例如："/public/user/create"
	 * @return
	 */
	public boolean allowAccess(String uri);
}
