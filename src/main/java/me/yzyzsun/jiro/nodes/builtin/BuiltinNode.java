package me.yzyzsun.jiro.nodes.builtin;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.runtime.JiroException;

@GenerateNodeFactory
@NodeChild(value = "arguments", type = ExpressionNode[].class)
public abstract class BuiltinNode extends ExpressionNode {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            return execute(frame);
        } catch (UnsupportedSpecializationException ex) {
            throw new JiroException(ex.getSuppliedValues().toString(), ex.getNode());
        }
    }

    protected abstract Object execute(VirtualFrame frame);
}
