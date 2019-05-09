package me.yzyzsun.jiro.nodes;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.RootNode;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.runtime.JiroContext;
import me.yzyzsun.jiro.runtime.JiroModule;
import me.yzyzsun.jiro.runtime.JiroNil;

public class EvalRootNode extends RootNode {
    private final JiroModule module;
    @CompilationFinal private boolean registered = false;
    private final TruffleLanguage.ContextReference<JiroContext> reference;
    @Child private DirectCallNode mainCallNode;

    public EvalRootNode(Jiro language, RootCallTarget root, JiroModule module) {
        super(null);
        this.reference = language.getContextReference();
        this.mainCallNode = root != null ? DirectCallNode.create(root) : null;
        this.module = module;
    }

    @Override
    public String toString() {
        return "root@eval";
    }

    @Override
    public Object execute(VirtualFrame frame) {
        if (!registered) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            reference.get().registerModule(module);
            registered = true;
        }
        if (mainCallNode == null) {
            return JiroNil.SINGLETON;
        } else {
            return mainCallNode.call();
        }
    }
}
