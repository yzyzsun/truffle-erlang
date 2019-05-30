package me.yzyzsun.jiro.runtime;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class TailCallException extends ControlFlowException {
    public final JiroFunction function;
    public final Object[] arguments;

    public TailCallException(JiroFunction function, Object[] arguments) {
        this.function = function;
        this.arguments = arguments;
    }
}
