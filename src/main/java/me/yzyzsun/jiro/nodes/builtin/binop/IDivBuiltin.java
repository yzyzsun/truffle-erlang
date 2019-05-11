package me.yzyzsun.jiro.nodes.builtin.binop;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import lombok.val;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;
import me.yzyzsun.jiro.runtime.JiroBigInteger;
import me.yzyzsun.jiro.runtime.JiroException;

@BuiltinInfo(identifier = "div", arity = 2)
public abstract class IDivBuiltin extends BuiltinNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    public long idiv(long left, long right) {
        val result = left / right;
        // idiv overflows if left is Long.MIN_VALUE and right is -1
        if ((left & right & result) < 0) throw new ArithmeticException("long overflow");
        return result;
    }

    @Specialization @TruffleBoundary
    public JiroBigInteger idiv(JiroBigInteger left, JiroBigInteger right) {
        return new JiroBigInteger(left.getValue().divide(right.getValue()));
    }

    @Fallback
    public Object badArgument(Object left, Object right) {
        throw JiroException.badArgument("expected two integers", this, left, right);
    }
}
