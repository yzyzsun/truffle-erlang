package me.yzyzsun.jiro.runtime;

import com.oracle.truffle.api.RootCallTarget;
import lombok.Getter;
import lombok.val;
import me.yzyzsun.jiro.Jiro;

import java.util.*;

public class JiroModule extends JiroObject {
    private final Jiro language;
    @Getter private final String name;
    private final Set<JiroFunctionName> exports;
    private final Map<JiroFunctionName, JiroFunction> functions = new HashMap<>();

    public JiroModule(Jiro language, String name, Set<JiroFunctionName> exports) {
        this.language = language;
        this.name = name;
        this.exports = exports;
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

    public JiroFunction getFunction(JiroFunctionName functionName) {
        return functions.get(functionName);
    }

    public List<JiroFunction> getFunctions() {
        val result = new ArrayList<JiroFunction>(functions.values());
        result.sort(Comparator.comparing(JiroFunction::toString));
        return result;
    }
}
