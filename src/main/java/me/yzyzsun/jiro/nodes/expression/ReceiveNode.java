package me.yzyzsun.jiro.nodes.expression;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import lombok.val;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.JiroContext;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.nodes.pattern.ClauseNode;
import me.yzyzsun.jiro.runtime.JiroActor;
import me.yzyzsun.jiro.runtime.JiroPid;
import me.yzyzsun.jiro.runtime.ReceiveException;

import java.time.Duration;

public class ReceiveNode extends ExpressionNode {
    @Children private ClauseNode[] clauses;
    @Child private ExpressionNode expiry;
    private Duration timeout;
    @Child private ExpressionNode after;
    private MaterializedFrame frame;
    private ContextReference<JiroContext> reference;

    public ReceiveNode(Jiro language, ClauseNode[] clauses, ExpressionNode expiry, ExpressionNode after) {
        this.clauses = clauses;
        this.expiry = expiry;
        this.after = after;
        reference = language.getContextReference();
    }

    public JiroPid createActor() {
        val actor = reference.get().getActorSystem().actorOf(JiroActor.props(clauses, timeout, after, frame));
        return new JiroPid(actor);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        val t = expiry.executeGeneric(frame);
        if (t instanceof Long) timeout = Duration.ofMillis((long) t);
        this.frame = frame.materialize();
        throw new ReceiveException(this);
    }
}
