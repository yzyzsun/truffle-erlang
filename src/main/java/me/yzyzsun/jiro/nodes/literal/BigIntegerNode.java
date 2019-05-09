package me.yzyzsun.jiro.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.runtime.JiroBigInteger;

import java.math.BigInteger;

public class BigIntegerNode extends ExpressionNode {
    private final JiroBigInteger value;

    public BigIntegerNode(BigInteger value) {
        this.value = new JiroBigInteger(value);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
