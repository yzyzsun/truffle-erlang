package me.yzyzsun.jiro.nodes.builtin.binop;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;
import me.yzyzsun.jiro.runtime.JiroBigInteger;

@BuiltinInfo(identifier = "<", arity = 2)
public abstract class LTBuiltin extends BuiltinNode {
    @Specialization
    public boolean lt(long left, long right) {
        return left < right;
    }

    @Specialization @TruffleBoundary
    public boolean lt(JiroBigInteger left, JiroBigInteger right) {
        return left.compareTo(right) < 0;
    }

    @Specialization
    public boolean lt(double left, double right) {
        return left < right;
    }

    @Specialization
    public boolean lt(String left, String right) {
        return left.compareTo(right) < 0;
    }

    @Fallback @TruffleBoundary
    public boolean lt(Object left, Object right) {
        return Jiro.compare(left, right) < 0;
    }
}
