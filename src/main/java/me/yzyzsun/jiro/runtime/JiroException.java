package me.yzyzsun.jiro.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleException;
import com.oracle.truffle.api.nodes.Node;
import lombok.val;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JiroException extends RuntimeException implements TruffleException {
    private final Node location;

    @TruffleBoundary
    public JiroException(String message, Node location) {
        super(message);
        this.location = location;
    }

    @Override
    public Node getLocation() {
        return location;
    }

    public static JiroException badArgument(String message, Node node, Object... arguments) {
        val msg = new StringBuilder();
        val info = node.getClass().getAnnotation(BuiltinInfo.class);
        if (info != null) msg.append(info.identifier()).append('/').append(info.arity()).append(' ');
        msg.append("bad argument: ");
        if (!message.isEmpty()) msg.append(message).append(": ");
        Function<Object, String> toString = x -> x == null ? "null" : x.toString();
        msg.append(Arrays.stream(arguments).map(toString).collect(Collectors.joining(", ")));
        return new JiroException(msg.toString(), node);
    }
}
