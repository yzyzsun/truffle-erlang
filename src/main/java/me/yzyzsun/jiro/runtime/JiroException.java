package me.yzyzsun.jiro.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleException;
import com.oracle.truffle.api.nodes.Node;

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
}
