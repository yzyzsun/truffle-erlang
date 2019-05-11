package me.yzyzsun.jiro.nodes.builtin.binop;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;
import me.yzyzsun.jiro.runtime.JiroBigInteger;
import me.yzyzsun.jiro.runtime.JiroException;

@BuiltinInfo(identifier = "rem", arity = 2)
public abstract class RemBuiltin extends BuiltinNode {
    @Specialization
    public long rem(long left, long right) {
        return left % right;
    }

    @Specialization @TruffleBoundary
    public JiroBigInteger rem(JiroBigInteger left, JiroBigInteger right) {
        return new JiroBigInteger(left.getValue().remainder(right.getValue()));
    }

    @Fallback
    public Object badArgument(Object left, Object right) {
        throw JiroException.badArgument("expected two integers", this, left, right);
    }
}
