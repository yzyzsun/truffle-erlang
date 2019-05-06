package me.yzyzsun.jiro.runtime;

import com.oracle.truffle.api.TruffleLanguage;
import me.yzyzsun.jiro.Jiro;

import java.util.HashMap;
import java.util.Map;

public class JiroContext {
    private final Jiro language;
    private final TruffleLanguage.Env env;
    private final Map<String, JiroModule> modules = new HashMap<>();

    public JiroContext(Jiro language, TruffleLanguage.Env env) {
        this.language = language;
        this.env = env;
    }

    public void registerModule(JiroModule module) {
        modules.put(module.getName(), module);
    }

    public JiroModule getModule(String moduleName) {
        return modules.get(moduleName);
    }
}
