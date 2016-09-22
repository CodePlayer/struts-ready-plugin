package me.codeplayer.auth;

import java.lang.reflect.Method;

import com.opensymphony.xwork2.ActionInvocation;

public interface PermissionPolicy {

	String getCodeFromClass(ActionInvocation actionInvocation, Class<?> clazz);

	String getCodeFromMethod(ActionInvocation actionInvocation, Method method);
}
