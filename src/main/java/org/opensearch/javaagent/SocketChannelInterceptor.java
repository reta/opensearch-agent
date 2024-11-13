package org.opensearch.javaagent;

import java.lang.reflect.Method;
import java.net.SocketPermission;
import java.security.AccessController;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Origin;

public class SocketChannelInterceptor {
	@Advice.OnMethodEnter
	public static void intercept(@Origin Method method) throws Exception {
		AccessController.checkPermission(new SocketPermission("localhost", "connect"));
		throw new SecurityException();
	}
}
