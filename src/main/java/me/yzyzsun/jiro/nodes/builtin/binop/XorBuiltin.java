package me.yzyzsun.jiro.nodes.builtin.binop;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;
import me.yzyzsun.jiro.runtime.JiroException;

@BuiltinInfo(identifier = "xor", arity = 2)
public abstract class XorBuiltin extends BuiltinNode {
    @Specialization
    public boolean xor(boolean left, boolean right) {
        return left != right;
    }

    @Fallback
    public Object badArgument(Object left, Object right) {
        throw JiroException.badArgument("expected two boolean atoms", this, left, right);
    }
}
