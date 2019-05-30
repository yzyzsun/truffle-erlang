package me.yzyzsun.jiro.runtime;

import com.oracle.truffle.api.nodes.ControlFlowException;
import lombok.Getter;
import me.yzyzsun.jiro.nodes.expression.ReceiveNode;

public class ReceiveException extends ControlFlowException {
    @Getter private ReceiveNode node;

    public ReceiveException(ReceiveNode node) {
        this.node = node;
    }
}
