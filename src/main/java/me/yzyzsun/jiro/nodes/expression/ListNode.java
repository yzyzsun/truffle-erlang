package me.yzyzsun.jiro.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.runtime.JiroList;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ListNode extends ExpressionNode {
    @Children private final ExpressionNode[] nodes;

    public ListNode(ExpressionNode[] nodes) {
        this.nodes = nodes;
    }

    public ListNode(ExpressionNode[] car, ListNode cdr) {
        nodes = new ExpressionNode[car.length + cdr.nodes.length];
        System.arraycopy(car, 0, nodes, 0, car.length);
        System.arraycopy(cdr.nodes, 0, nodes, car.length, cdr.nodes.length);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return new JiroList(Arrays.stream(nodes).map(x -> x.executeGeneric(frame)).collect(Collectors.toList()));
    }
}
