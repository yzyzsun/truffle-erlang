package me.yzyzsun.jiro.nodes.builtin.binop;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;
import me.yzyzsun.jiro.runtime.JiroBigInteger;

@BuiltinInfo(identifier = ">", arity = 2)
public abstract class GTBuiltin extends BuiltinNode {
    @Specialization
    public boolean gt(long left, long right) {
        return left > right;
    }

    @Specialization @TruffleBoundary
    public boolean gt(JiroBigInteger left, JiroBigInteger right) {
        return left.compareTo(right) > 0;
    }

    @Specialization
    public boolean gt(double left, double right) {
        return left > right;
    }

    @Specialization
    public boolean gt(String left, String right) {
        return left.compareTo(right) > 0;
    }

    @Fallback @TruffleBoundary
    public boolean gt(Object left, Object right) {
        return Jiro.compare(left, right) > 0;
    }
}
