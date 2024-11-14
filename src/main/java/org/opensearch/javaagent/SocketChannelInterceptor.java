package org.opensearch.javaagent;

import java.lang.reflect.Method;
import java.net.SocketPermission;

import org.opensearch.secure_sm.SecureSM;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Origin;

public class SocketChannelInterceptor {
	@Advice.OnMethodEnter
	public static void intercept(@Origin Method method) throws Exception {
		final SecureSM sm = new SecureSM();
		sm.checkPermission(new SocketPermission("localhost", "connect"));
	}
}
