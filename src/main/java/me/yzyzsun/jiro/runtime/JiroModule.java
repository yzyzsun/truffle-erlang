package me.yzyzsun.jiro.runtime;

import com.oracle.truffle.api.RootCallTarget;
import lombok.Getter;
import lombok.val;
import me.yzyzsun.jiro.Jiro;

import java.util.*;

public class JiroModule extends JiroObject {
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
}
