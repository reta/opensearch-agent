package org.opensearch.javaagent;

import java.lang.instrument.Instrumentation;
import java.nio.channels.SocketChannel;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatcher.Junction;
import net.bytebuddy.matcher.ElementMatchers;

public class Agent {
	private static boolean loadedDynamically = false;

	public static void premain(String agentArguments, Instrumentation instrumentation) {
		System.out.print("Starting from premain");

		AgentBuilder agentBuilder = createAgentBuilder(instrumentation);
		agentBuilder.installOn(instrumentation);
	}

	public static void agentmain(String agentArguments, Instrumentation instrumentation) {
		System.out.print("Starting from agentmain");
		loadedDynamically = true;

		AgentBuilder agentBuilder = createAgentBuilder(instrumentation);
		agentBuilder.installOn(instrumentation);
	}

	private static AgentBuilder createAgentBuilder(Instrumentation inst) {
	     final Junction<TypeDescription> systemType = ElementMatchers.isSubTypeOf(SocketChannel.class);

	     final AgentBuilder.Transformer transformer =
	             (b, typeDescription, classLoader, module, pd) -> b
	                .visit(Advice.to(SocketChannelInterceptor.class).on(ElementMatchers.named("connect")
	                    .and(ElementMatchers.not(ElementMatchers.isAbstract()))));

	     // Disable a bunch of stuff and turn on redefine as the only option
	     final ByteBuddy byteBuddy = new ByteBuddy().with(Implementation.Context.Disabled.Factory.INSTANCE);
	     final AgentBuilder agentBuilder = new AgentBuilder.Default(byteBuddy)
             .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
             .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
             .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
             .ignore(ElementMatchers.none())
             .type(systemType)
             .transform(transformer);

	     return agentBuilder;
	}
}
