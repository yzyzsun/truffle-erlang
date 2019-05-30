package me.yzyzsun.jiro.nodes.builtin;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import lombok.val;
import me.yzyzsun.jiro.runtime.JiroException;
import me.yzyzsun.jiro.runtime.JiroFunction;
import me.yzyzsun.jiro.runtime.JiroPid;
import me.yzyzsun.jiro.runtime.ReceiveException;

@BuiltinInfo(identifier = "spawn", arity = 1)
public abstract class SpawnBuiltin extends BuiltinNode {
    @Specialization
    public JiroPid spawn(JiroFunction fun) {
        val callNode = DirectCallNode.create(fun.getCallTarget());
        try {
            callNode.call();
        } catch (ReceiveException ex) {
            val receiveNode = ex.getNode();
            return receiveNode.createActor();
        }
        return null;
    }

    @Fallback
    public Object badArgument(Object object) {
        throw JiroException.badArgument("expected fun", this, object);
    }
}
