package me.codeplayer.auth;

import java.lang.reflect.Method;

import com.opensymphony.xwork2.ActionInvocation;

import me.codeplayer.annotation.Permission;

public interface PermissionPolicy {

	String getCodeFromClass(ActionInvocation actionInvocation, Class<?> clazz, Permission permission);

	String getCodeFromMethod(ActionInvocation actionInvocation, Method method, Permission permission);
}
