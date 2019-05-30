package me.yzyzsun.jiro.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import me.yzyzsun.jiro.nodes.ExpressionNode;

public class SequencingNode extends ExpressionNode {
    @Child private ExpressionNode first, second;

    public SequencingNode(ExpressionNode first, ExpressionNode second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        first.executeGeneric(frame);
        return second.executeGeneric(frame);
    }

    @Override
    public void markAsTail() {
        second.markAsTail();
    }
}
