package me.yzyzsun.jiro.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import me.yzyzsun.jiro.nodes.ExpressionNode;

public class IntegerNode extends ExpressionNode {
    private final long value;

    public IntegerNode(long value) {
        this.value = value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
