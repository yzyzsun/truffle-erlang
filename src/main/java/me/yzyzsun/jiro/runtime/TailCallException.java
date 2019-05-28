package me.yzyzsun.jiro.runtime;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class TailCallException extends ControlFlowException {
    public final Object function;
    public final Object[] arguments;

    public TailCallException(Object function, Object[] arguments) {
        this.function = function;
        this.arguments = arguments;
    }
}
