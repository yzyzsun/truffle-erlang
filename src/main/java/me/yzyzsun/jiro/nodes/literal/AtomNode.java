package me.yzyzsun.jiro.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import lombok.Getter;
import me.yzyzsun.jiro.nodes.ExpressionNode;

public class AtomNode extends ExpressionNode {
    @Getter private final String value;

    public AtomNode(String value) {
        this.value = value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
