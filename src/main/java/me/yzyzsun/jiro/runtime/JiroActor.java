package me.yzyzsun.jiro.runtime;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import com.oracle.truffle.api.frame.MaterializedFrame;
import lombok.val;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.nodes.pattern.ClauseNode;

import java.time.Duration;

public class JiroActor extends AbstractActor {
    public static Props props(ClauseNode[] clauses, Duration timeout, ExpressionNode after, MaterializedFrame frame) {
        return Props.create(JiroActor.class, () -> new JiroActor(clauses, timeout, after, frame));
    }

    private ClauseNode[] clauses;
    private ExpressionNode after;
    private MaterializedFrame frame;

    public JiroActor(ClauseNode[] clauses, Duration timeout, ExpressionNode after, MaterializedFrame frame) {
        this.clauses = clauses;
        this.after = after;
        this.frame = frame;
        if (timeout != null) getContext().setReceiveTimeout(timeout);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ReceiveTimeout.class, x -> {
                    getContext().cancelReceiveTimeout();
                    after.executeGeneric(frame);
                    getContext().stop(getSelf());
                })
                .matchAny(x -> {
                    for (val clause : clauses) {
                        if (clause.pattern.match(x, frame)) {
                            clause.expression.executeGeneric(frame);
                            break;
                        }
                    }
                })
                .build();
    }
}
