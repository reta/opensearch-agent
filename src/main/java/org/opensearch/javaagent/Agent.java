package org.opensearch.javaagent;

import java.lang.instrument.Instrumentation;
import java.nio.channels.SocketChannel;
import java.util.Map;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatcher.Junction;
import net.bytebuddy.matcher.ElementMatchers;

public class Agent {

	public static void premain(String agentArguments, Instrumentation instrumentation) throws Exception {
		initAgent(instrumentation);
	}

	public static void agentmain(String agentArguments, Instrumentation instrumentation) throws Exception {
		initAgent(instrumentation);
	}

	private static AgentBuilder createAgentBuilder(Instrumentation inst) throws Exception {
	     final Junction<TypeDescription> systemType = ElementMatchers.isSubTypeOf(SocketChannel.class);

	     final AgentBuilder.Transformer transformer =
	             (b, typeDescription, classLoader, module, pd) -> b
	                .visit(Advice.to(SocketChannelInterceptor.class).on(ElementMatchers.named("connect")
	                    .and(ElementMatchers.not(ElementMatchers.isAbstract()))));

          ClassInjector.UsingUnsafe.ofBootLoader().inject(Map.of(
              new TypeDescription.ForLoadedType(StackCallerChainExtractor.class), ClassFileLocator.ForClassLoader.read(StackCallerChainExtractor.class)));

	     final ByteBuddy byteBuddy = new ByteBuddy().with(Implementation.Context.Disabled.Factory.INSTANCE);
	     final AgentBuilder agentBuilder = new AgentBuilder.Default(byteBuddy)
             .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
             .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
             .with(AgentBuilder.RedefinitionStrategy.Listener.StreamWriting.toSystemError())
             .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
             .ignore(ElementMatchers.none())
             .type(systemType)
             .transform(transformer);

	     return agentBuilder;
	}
	
	private static void initAgent(Instrumentation instrumentation) throws Exception {
		AgentBuilder agentBuilder = createAgentBuilder(instrumentation);
		agentBuilder.installOn(instrumentation);
	}
}
