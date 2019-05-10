package me.yzyzsun.jiro;

import com.oracle.truffle.api.TruffleLanguage;
import lombok.Getter;
import lombok.var;
import me.yzyzsun.jiro.nodes.builtin.io.FormatBuiltinFactory;
import me.yzyzsun.jiro.nodes.builtin.io.GetLineBuiltinFactory;
import me.yzyzsun.jiro.runtime.JiroModule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class JiroContext {
    private final Jiro language;
    private final TruffleLanguage.Env env;
    @Getter private final BufferedReader input;
    @Getter private final PrintWriter output;
    private final Map<String, JiroModule> modules = new HashMap<>();

    public JiroContext(Jiro language, TruffleLanguage.Env env) {
        this.language = language;
        this.env = env;
        input = new BufferedReader(new InputStreamReader(env.in()));
        output = new PrintWriter(env.out());
        installBuiltins();
    }

    public void registerModule(JiroModule module) {
        modules.put(module.getName(), module);
    }

    public JiroModule getModule(String moduleName) {
        return modules.get(moduleName);
    }

    private void installBuiltins() {
        var module = new JiroModule(language, "io");
        module.installBuiltin(FormatBuiltinFactory.getInstance());
        module.installBuiltin(GetLineBuiltinFactory.getInstance());
        registerModule(module);
    }
}
