package me.yzyzsun.jiro.nodes.builtin.binop;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;
import me.yzyzsun.jiro.runtime.JiroException;

@BuiltinInfo(identifier = "not", arity = 1)
public abstract class NotBuiltin extends BuiltinNode {
    @Specialization
    public boolean not(boolean value) {
        return !value;
    }

    @Fallback
    public Object badArgument(Object value) {
        throw JiroException.badArgument("expected a boolean atom", this, value);
    }
}
