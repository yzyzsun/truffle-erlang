package me.yzyzsun.jiro.nodes.expression;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.runtime.JiroContext;
import me.yzyzsun.jiro.runtime.JiroFunction;
import me.yzyzsun.jiro.runtime.JiroFunctionName;

public class FunctionNameNode extends ExpressionNode {
    private JiroFunctionName functionName;
    private final ContextReference<JiroContext> reference;
    @CompilationFinal private JiroFunction cachedFunction;

    public FunctionNameNode(Jiro language, String identifier, int arity) {
        this.functionName = new JiroFunctionName(identifier, arity);
        this.reference = language.getContextReference();
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        if (cachedFunction == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            cachedFunction = reference.get().getFunctionRegistry().lookup(functionName, true);
        }
        return cachedFunction;
    }
}
