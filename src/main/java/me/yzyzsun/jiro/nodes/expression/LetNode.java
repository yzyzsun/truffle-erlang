package me.yzyzsun.jiro.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import lombok.val;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.nodes.local.BindVariableNode;

public class LetNode extends ExpressionNode {
    @Children private BindVariableNode[] bindNodes;
    @Child private ExpressionNode exprNode;

    public LetNode(BindVariableNode[] bindNodes, ExpressionNode exprNode) {
        this.bindNodes = bindNodes;
        this.exprNode = exprNode;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        for (val bindNode : bindNodes) bindNode.executeGeneric(frame);
        return exprNode.executeGeneric(frame);
        // TODO: Remove frame slots when leaving let nodes
    }

    @Override
    public void markAsTail() {
        exprNode.markAsTail();
    }
}
