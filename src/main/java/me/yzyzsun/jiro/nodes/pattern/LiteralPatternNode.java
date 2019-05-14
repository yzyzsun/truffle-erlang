package me.yzyzsun.jiro.nodes.pattern;

import com.oracle.truffle.api.frame.VirtualFrame;
import lombok.val;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.nodes.ExpressionNode;

public class LiteralPatternNode extends PatternNode {
    @Child private ExpressionNode literal;

    public LiteralPatternNode(ExpressionNode literal) {
        this.literal = literal;
    }

    @Override
    public boolean match(Object obj, VirtualFrame frame) {
        val value = literal.executeGeneric(frame);
        return Jiro.exactlyEqual(obj, value);
    }
}
