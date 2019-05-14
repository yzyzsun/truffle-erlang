package me.yzyzsun.jiro.runtime;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.utilities.CyclicAssumption;
import lombok.Getter;
import lombok.val;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.nodes.UndefinedRootNode;

@ExportLibrary(InteropLibrary.class)
public class JiroFunction implements TruffleObject {
    public static final int INLINE_CACHE_SIZE = 3;

    @Getter private final JiroFunctionName functionName;
    private RootCallTarget callTarget;
    private final CyclicAssumption callTargetStable;

    public JiroFunction(Jiro language, JiroFunctionName functionName) {
        this.functionName = functionName;
        val name = functionName.toString();
        this.callTarget = Truffle.getRuntime().createCallTarget(new UndefinedRootNode(language, name));
        this.callTargetStable = new CyclicAssumption(name);
    }

    public RootCallTarget getCallTarget() {
        return callTarget;
    }

    public void setCallTarget(RootCallTarget callTarget) {
        this.callTarget = callTarget;
        callTargetStable.invalidate();
    }

    public Assumption getCallTargetStable() {
        return callTargetStable.getAssumption();
    }

    @Override
    public String toString() {
        return functionName.toString();
    }

    public SourceSection getDeclaredLocation() {
        return callTarget.getRootNode().getSourceSection();
    }

    @ExportMessage
    boolean isExecutable() {
        return true;
    }

    @ExportMessage
    abstract static class Execute {
        @Specialization(limit = "INLINE_CACHE_SIZE",
                        guards = "function.getCallTarget() == cachedTarget",
                        assumptions = "callTargetStable")
        protected static Object doDirect(JiroFunction function, Object[] arguments,
                                         @Cached("function.getCallTargetStable()") Assumption callTargetStable,
                                         @Cached("function.getCallTarget()") RootCallTarget cachedTarget,
                                         @Cached("create(cachedTarget)") DirectCallNode callNode) {
            return callNode.call(arguments);
        }

        @Specialization(replaces = "doDirect")
        protected static Object doIndirect(JiroFunction function, Object[] arguments, @Cached IndirectCallNode callNode) {
            return callNode.call(function.getCallTarget(), arguments);
        }
    }
}
