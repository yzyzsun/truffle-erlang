package me.yzyzsun.jiro.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.runtime.JiroNil;

public class NilNode extends ExpressionNode {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return JiroNil.SINGLETON;
    }
}
