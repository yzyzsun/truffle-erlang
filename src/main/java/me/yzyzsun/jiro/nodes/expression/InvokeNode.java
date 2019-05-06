package me.yzyzsun.jiro.nodes.expression;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.runtime.JiroException;

public class InvokeNode extends ExpressionNode {
    @Child private ExpressionNode functionNode;
    @Children private final ExpressionNode[] argumentNodes;
    @Child private InteropLibrary library = InteropLibrary.getFactory().createDispatched(3);

    public InvokeNode(ExpressionNode functionNode, ExpressionNode[] argumentNodes) {
        this.functionNode = functionNode;
        this.argumentNodes = argumentNodes;
    }

    @Override @ExplodeLoop
    public Object executeGeneric(VirtualFrame frame) {
        val function = functionNode.executeGeneric(frame);
        CompilerAsserts.compilationConstant(argumentNodes.length);
        val arguments = new Object[argumentNodes.length];
        for (var i = 0; i < argumentNodes.length; ++i) {
            arguments[i] = argumentNodes[i].executeGeneric(frame);
        }
        try {
            return library.execute(function, arguments);
        } catch (ArityException | UnsupportedTypeException | UnsupportedMessageException e) {
            throw new JiroException("invocation failed: " + function, functionNode);
        }
    }
}
