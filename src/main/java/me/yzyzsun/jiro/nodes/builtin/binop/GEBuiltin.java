package me.yzyzsun.jiro.nodes.builtin.binop;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;
import me.yzyzsun.jiro.runtime.JiroBigInteger;

@BuiltinInfo(identifier = ">=", arity = 2)
public abstract class GEBuiltin extends BuiltinNode {
    @Specialization
    public boolean ge(long left, long right) {
        return left >= right;
    }

    @Specialization @TruffleBoundary
    public boolean ge(JiroBigInteger left, JiroBigInteger right) {
        return left.compareTo(right) >= 0;
    }

    @Specialization
    public boolean ge(double left, double right) {
        return left >= right;
    }

    @Specialization
    public boolean ge(String left, String right) {
        return left.compareTo(right) >= 0;
    }

    @Fallback @TruffleBoundary
    public boolean ge(Object left, Object right) {
        return Jiro.compare(left, right) >= 0;
    }
}
