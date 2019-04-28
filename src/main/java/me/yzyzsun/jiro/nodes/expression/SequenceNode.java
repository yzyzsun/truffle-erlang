package me.yzyzsun.jiro.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.runtime.JiroSequence;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SequenceNode extends ExpressionNode {
    @Children private final ExpressionNode[] nodes;

    public SequenceNode(ExpressionNode[] nodes) {
        this.nodes = nodes;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return new JiroSequence(Arrays.stream(nodes).map(x -> x.executeGeneric(frame)).collect(Collectors.toList()));
    }
}
