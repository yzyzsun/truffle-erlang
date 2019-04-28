package me.yzyzsun.jiro.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.runtime.JiroCons;

public class ConsNode extends ExpressionNode {
    @Child private ExpressionNode car, cdr;

    public ConsNode(ExpressionNode car, ExpressionNode cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return new JiroCons(car.executeGeneric(frame), cdr.executeGeneric(frame));
    }
}
