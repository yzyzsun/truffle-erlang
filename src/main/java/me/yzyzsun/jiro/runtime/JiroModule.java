package me.yzyzsun.jiro.runtime;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.FrameDescriptor;
import lombok.Getter;
import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.nodes.JiroRootNode;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;
import me.yzyzsun.jiro.nodes.local.ReadArgumentNode;

import java.util.*;

public class JiroModule {
    private final Jiro language;
    @Getter private final String name;
    private final Set<JiroFunctionName> exports = new HashSet<>();
    private final Map<JiroFunctionName, JiroFunction> functions = new HashMap<>();

    public JiroModule(Jiro language, String name) {
        this.language = language;
        this.name = name;
    }

    public void export(JiroFunctionName functionName) {
        exports.add(functionName);
    }

    public JiroFunction registerFunction(JiroFunctionName functionName, RootCallTarget callTarget) {
        val function = new JiroFunction(language, functionName);
        functions.put(functionName, function);
        function.setCallTarget(callTarget);
        return function;
    }

    public void registerFunctions(Map<JiroFunctionName, RootCallTarget> newFunctions) {
        for (val entry : newFunctions.entrySet()) {
            registerFunction(entry.getKey(), entry.getValue());
        }
    }

    public JiroFunction getFunction(JiroFunctionName functionName, boolean interModule) {
        if (interModule && !exports.contains(functionName)) return null;
        return functions.get(functionName);
    }

    public List<JiroFunction> getFunctions() {
        val result = new ArrayList<JiroFunction>(functions.values());
        result.removeIf(x -> !exports.contains(x.getFunctionName()));
        result.sort(Comparator.comparing(JiroFunction::toString));
        return result;
    }

    public void installBuiltin(NodeFactory<? extends BuiltinNode> factory) {
        val argc = factory.getExecutionSignature().size();
        val nodes = new ExpressionNode[argc];
        for (var i = 0; i < argc; ++i) {
            nodes[i] = new ReadArgumentNode(i);
        }
        val builtin = factory.createNode((Object) nodes);
        val root = new JiroRootNode(language, new FrameDescriptor(), builtin, null);
        val functionName = lookupBuiltinFunctionName(builtin.getClass());
        assert functionName != null;
        root.setName(functionName.toString());
        registerFunction(functionName, Truffle.getRuntime().createCallTarget(root));
        export(functionName);
    }

    private static JiroFunctionName lookupBuiltinFunctionName(Class<?> klass) {
        if (klass == null) return null;
        val info = klass.getAnnotation(BuiltinInfo.class);
        if (info == null) return null;
        return new JiroFunctionName(info.identifier(), info.arity());
    }
}
