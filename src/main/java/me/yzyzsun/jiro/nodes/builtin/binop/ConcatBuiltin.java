package me.yzyzsun.jiro.nodes.builtin.binop;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;
import me.yzyzsun.jiro.runtime.JiroException;
import me.yzyzsun.jiro.runtime.JiroList;

@BuiltinInfo(identifier = "++", arity = 2)
public abstract class ConcatBuiltin extends BuiltinNode {
    @Specialization @TruffleBoundary
    public JiroList concat(JiroList left, JiroList right) {
        return left.concat(right);
    }

    @Fallback
    public Object badArgument(Object left, Object right) {
        throw JiroException.badArgument("expected two lists", this, left, right);
    }
}
