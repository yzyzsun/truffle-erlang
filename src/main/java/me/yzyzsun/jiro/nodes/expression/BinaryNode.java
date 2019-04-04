package me.yzyzsun.jiro.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.runtime.JiroBinary;

import java.util.List;

public class BinaryNode extends ExpressionNode {
    private final List<ExpressionNode> nodes;

    public BinaryNode(List<ExpressionNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        val bytes = new byte[nodes.size()];
        for (var i = 0; i < bytes.length; ++i) {
            bytes[i] = ((Number) nodes.get(i).executeGeneric(frame)).byteValue();
        }
        return new JiroBinary(bytes);
    }
}
