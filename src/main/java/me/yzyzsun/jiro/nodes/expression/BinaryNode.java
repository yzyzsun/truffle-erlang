package me.yzyzsun.jiro.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.runtime.JiroBinary;

public class BinaryNode extends ExpressionNode {
    @Children private final ExpressionNode[] nodes;

    public BinaryNode(ExpressionNode[] nodes) {
        this.nodes = nodes;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        val bytes = new byte[nodes.length];
        for (var i = 0; i < bytes.length; ++i) {
            bytes[i] = ((Number) nodes[i].executeGeneric(frame)).byteValue();
        }
        return new JiroBinary(bytes);
    }
}
