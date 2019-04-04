package me.yzyzsun.jiro.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import me.yzyzsun.jiro.nodes.ExpressionNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListNode extends ExpressionNode {
    private final List<ExpressionNode> nodes;

    public ListNode(List<ExpressionNode> nodes) {
        this.nodes = nodes;
    }

    public ListNode(List<ExpressionNode> car, ListNode cdr) {
        this.nodes = new ArrayList<>();
        this.nodes.addAll(car);
        this.nodes.addAll(cdr.nodes);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return nodes.stream().map(x -> x.executeGeneric(frame)).collect(Collectors.toList());
    }
}
