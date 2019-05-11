package me.yzyzsun.jiro.nodes.builtin;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.frame.VirtualFrame;
import me.yzyzsun.jiro.nodes.ExpressionNode;

@GenerateNodeFactory
@NodeChild(value = "arguments", type = ExpressionNode[].class)
public abstract class BuiltinNode extends ExpressionNode {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return execute(frame);
    }

    protected abstract Object execute(VirtualFrame frame);
}
