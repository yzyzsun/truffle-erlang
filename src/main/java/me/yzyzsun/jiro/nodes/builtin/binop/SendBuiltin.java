package me.yzyzsun.jiro.nodes.builtin.binop;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;
import me.yzyzsun.jiro.runtime.JiroException;
import me.yzyzsun.jiro.runtime.JiroPid;

@BuiltinInfo(identifier = "!", arity = 2)
public abstract class SendBuiltin extends BuiltinNode {
    @Specialization
    public Object send(JiroPid left, Object right) {
        left.send(right);
        return right;
    }

    @Fallback
    public Object badArgument(Object left, Object right) {
        throw JiroException.badArgument("expected pid and message", this, left, right);
    }
}
