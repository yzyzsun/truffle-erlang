package me.yzyzsun.jiro.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import lombok.Getter;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.runtime.JiroFunction;

public class FunNode extends ExpressionNode {
    @Getter private JiroFunction function;

    public FunNode(JiroFunction function) {
        this.function = function;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // TODO: `frame.materialize()` should be captured
        return function;
    }
}
