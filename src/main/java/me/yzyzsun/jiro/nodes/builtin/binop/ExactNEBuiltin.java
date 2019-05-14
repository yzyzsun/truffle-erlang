package me.yzyzsun.jiro.nodes.builtin.binop;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;

@BuiltinInfo(identifier = "=/=", arity = 2)
public abstract class ExactNEBuiltin extends BuiltinNode {
    @Specialization @TruffleBoundary
    public boolean exactNE(Object left, Object right) {
        return !Jiro.exactlyEqual(left, right);
    }
}
