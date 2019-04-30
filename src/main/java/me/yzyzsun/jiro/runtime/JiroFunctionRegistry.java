package me.yzyzsun.jiro.runtime;

import com.oracle.truffle.api.RootCallTarget;
import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.Jiro;

import java.util.*;

public class JiroFunctionRegistry {
    private final Jiro language;
    private final Map<JiroFunctionName, JiroFunction> functions = new HashMap<>();

    public JiroFunctionRegistry(Jiro language) {
        this.language = language;
    }

    public JiroFunction lookup(JiroFunctionName functionName, boolean createIfAbsent) {
        var result = functions.get(functionName);
        if (result == null && createIfAbsent) {
            result = new JiroFunction(language, functionName);
            functions.put(functionName, result);
        }
        return result;
    }

    public JiroFunction register(JiroFunctionName functionName, RootCallTarget callTarget) {
        val function = lookup(functionName, true);
        function.setCallTarget(callTarget);
        return function;
    }

    public void register(Map<JiroFunctionName, RootCallTarget> newFunctions) {
        for (val entry : newFunctions.entrySet()) {
            register(entry.getKey(), entry.getValue());
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
