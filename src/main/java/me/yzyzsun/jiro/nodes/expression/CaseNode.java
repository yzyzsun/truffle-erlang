package me.yzyzsun.jiro.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import lombok.val;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.nodes.pattern.ClauseNode;
import me.yzyzsun.jiro.runtime.JiroException;

public class CaseNode extends ExpressionNode {
    @Child private ExpressionNode expression;
    @Children private ClauseNode[] clauses;

    public CaseNode(ExpressionNode expression, ClauseNode[] clauses) {
        this.expression = expression;
        this.clauses = clauses;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        val exp = expression.executeGeneric(frame);
        for (val clause : clauses) {
            if (!clause.pattern.match(exp, frame)) continue;
            val guard = clause.guard.executeGeneric(frame);
            if (!(guard instanceof Boolean)) throw new JiroException("clause guard should be boolean", this);
            if (!((Boolean) guard)) continue;
            return clause.expression.executeGeneric(frame);
        }
        throw new JiroException("pattern matching failed: " + exp, this);
        // TODO: Revert bindings if a guard fails or pattern matching fails half way
        // TODO: Remove frame slots when leaving case nodes
    }
}
