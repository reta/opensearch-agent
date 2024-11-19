package org.opensearch.javaagent;

import java.lang.StackWalker.StackFrame;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StackCallerChainExtractor implements Function<Stream<StackFrame>, List<Class<?>>> {
	@Override
	public List<Class<?>> apply(Stream<StackFrame> frames) {
		return frames
			.map(StackFrame::getDeclaringClass)
			.collect(Collectors.toList());
	}
}

