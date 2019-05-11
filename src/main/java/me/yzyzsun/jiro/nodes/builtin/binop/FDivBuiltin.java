package me.yzyzsun.jiro.nodes.builtin.binop;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;
import me.yzyzsun.jiro.runtime.JiroException;

@BuiltinInfo(identifier = "/", arity = 2)
public abstract class FDivBuiltin extends BuiltinNode {
    @Specialization
    public double fdiv(double left, double right) {
        return left / right;
    }

    @Fallback
    public Object badArgument(Object left, Object right) {
        throw JiroException.badArgument("expected two numbers", this, left, right);
    }
}
