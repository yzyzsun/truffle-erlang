package me.yzyzsun.jiro.nodes.builtin.binop;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;
import me.yzyzsun.jiro.runtime.JiroBigInteger;
import me.yzyzsun.jiro.runtime.JiroException;

@BuiltinInfo(identifier = "+", arity = 2)
public abstract class AddBuiltin extends BuiltinNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    public long add(long left, long right) {
        return Math.addExact(left, right);
    }

    @Specialization @TruffleBoundary
    public JiroBigInteger add(JiroBigInteger left, JiroBigInteger right) {
        return new JiroBigInteger(left.getValue().add(right.getValue()));
    }

    @Specialization
    public double add(double left, double right) {
        return left + right;
    }

    @Fallback
    public Object badArgument(Object left, Object right) {
        throw JiroException.badArgument("expected two numbers", this, left, right);
    }
}
