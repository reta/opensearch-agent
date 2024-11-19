package org.opensearch.javaagent;

import java.lang.StackWalker.Option;
import java.lang.reflect.Method;
import java.util.List;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Origin;

public class SocketChannelInterceptor {
	@Advice.OnMethodEnter
	public static void intercept(@Advice.AllArguments Object[] args, @Origin Method method) throws Exception {
		final StackWalker walker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);

		final Class<?> caller = walker.getCallerClass();
		final List<Class<?>> callers = walker.walk(new StackCallerChainExtractor()); 
		
		System.out.println("Caller: " + caller + " thread group" + Thread.currentThread().getThreadGroup() + ", connects to " + args[0]);
		for (Class<?> c: callers) {
			System.out.println("Class " + c.getName());
		}
	}
}
