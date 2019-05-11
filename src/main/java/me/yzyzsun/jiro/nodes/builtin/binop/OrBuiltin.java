package me.yzyzsun.jiro.nodes.builtin.binop;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;
import me.yzyzsun.jiro.runtime.JiroException;

@BuiltinInfo(identifier = "or", arity = 2)
public abstract class OrBuiltin extends BuiltinNode {
    @Specialization
    public boolean or(boolean left, boolean right) {
        return left || right;
    }

    @Fallback
    public Object badArgument(Object left, Object right) {
        throw JiroException.badArgument("expected two boolean atoms", this, left, right);
    }
}
