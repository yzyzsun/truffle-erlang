package me.yzyzsun.jiro.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.runtime.JiroTuple;

import java.util.List;
import java.util.stream.Collectors;

public class TupleNode extends ExpressionNode {
    private final List<ExpressionNode> nodes;

    public TupleNode(List<ExpressionNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return new JiroTuple(nodes.stream().map(x -> x.executeGeneric(frame)).collect(Collectors.toList()));
    }
}
