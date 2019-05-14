package me.yzyzsun.jiro.nodes.pattern;

import com.oracle.truffle.api.nodes.Node;
import me.yzyzsun.jiro.nodes.ExpressionNode;

public class ClauseNode extends Node {
    @Child public PatternNode pattern;
    @Child public ExpressionNode guard;
    @Child public ExpressionNode expression;

    public ClauseNode(PatternNode pattern, ExpressionNode guard, ExpressionNode expression) {
        this.pattern = pattern;
        this.guard = guard;
        this.expression = expression;
    }
}
