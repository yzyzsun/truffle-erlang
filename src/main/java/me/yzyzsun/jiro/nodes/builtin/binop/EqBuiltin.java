package me.yzyzsun.jiro.nodes.builtin.binop;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;
import me.yzyzsun.jiro.runtime.JiroBigInteger;

@BuiltinInfo(identifier = "==", arity = 2)
public abstract class EqBuiltin extends BuiltinNode {
    @Specialization
    public boolean eq(long left, long right) {
        return left == right;
    }

    @Specialization @TruffleBoundary
    public boolean eq(JiroBigInteger left, JiroBigInteger right) {
        return left.equals(right);
    }

    @Specialization
    public boolean eq(double left, double right) {
        return left == right;
    }

    @Specialization
    public boolean eq(String left, String right) {
        return left.equals(right);
    }

    @Fallback @TruffleBoundary
    public boolean eq(Object left, Object right) {
        return Jiro.compare(left, right) == 0;
    }
}
