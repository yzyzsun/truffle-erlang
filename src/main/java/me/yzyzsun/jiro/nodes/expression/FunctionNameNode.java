package me.yzyzsun.jiro.nodes.expression;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import lombok.val;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.runtime.*;

public class FunctionNameNode extends ExpressionNode {
    private final String moduleName;
    private final JiroFunctionName functionName;
    private final ContextReference<JiroContext> reference;
    @CompilationFinal private JiroFunction cachedFunction;

    public FunctionNameNode(Jiro language, String moduleName, String identifier, int arity) {
        this.moduleName = moduleName;
        functionName = new JiroFunctionName(identifier, arity);
        reference = language.getContextReference();
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        if (cachedFunction == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            val module = reference.get().getModule(moduleName);
            cachedFunction = module.getFunction(functionName);
            if (cachedFunction == null) throw new JiroException("undefined function: " + functionName, this);
        }
        return cachedFunction;
    }
}
