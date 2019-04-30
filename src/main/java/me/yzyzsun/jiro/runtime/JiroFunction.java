package me.yzyzsun.jiro.runtime;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.utilities.CyclicAssumption;
import lombok.Getter;
import lombok.val;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.nodes.UndefinedRootNode;

public class JiroFunction extends JiroObject {
    @Getter private final JiroFunctionName functionName;
    @Getter private RootCallTarget callTarget;
    private final CyclicAssumption callTargetStable;

    JiroFunction(Jiro language, JiroFunctionName functionName) {
        this.functionName = functionName;
        val name = functionName.toString();
        this.callTarget = Truffle.getRuntime().createCallTarget(new UndefinedRootNode(language, name));
        this.callTargetStable = new CyclicAssumption(name);
    }

    void setCallTarget(RootCallTarget callTarget) {
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
}
