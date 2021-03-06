package me.yzyzsun.jiro;

import akka.actor.ActorSystem;
import com.oracle.truffle.api.TruffleLanguage;
import lombok.Getter;
import lombok.Setter;
import lombok.var;
import me.yzyzsun.jiro.nodes.builtin.FormatBuiltinFactory;
import me.yzyzsun.jiro.nodes.builtin.GetLineBuiltinFactory;
import me.yzyzsun.jiro.nodes.builtin.SpawnBuiltinFactory;
import me.yzyzsun.jiro.nodes.builtin.binop.*;
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
    @Getter @Setter private ActorSystem actorSystem;

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
        module = new JiroModule(language, "erlang");
        module.installBuiltin(AddBuiltinFactory.getInstance());
        module.installBuiltin(SubBuiltinFactory.getInstance());
        module.installBuiltin(MulBuiltinFactory.getInstance());
        module.installBuiltin(FDivBuiltinFactory.getInstance());
        module.installBuiltin(IDivBuiltinFactory.getInstance());
        module.installBuiltin(RemBuiltinFactory.getInstance());
        module.installBuiltin(NotBuiltinFactory.getInstance());
        module.installBuiltin(AndBuiltinFactory.getInstance());
        module.installBuiltin(OrBuiltinFactory.getInstance());
        module.installBuiltin(XorBuiltinFactory.getInstance());
        module.installBuiltin(EqBuiltinFactory.getInstance());
        module.installBuiltin(NEBuiltinFactory.getInstance());
        module.installBuiltin(ExactEqBuiltinFactory.getInstance());
        module.installBuiltin(ExactNEBuiltinFactory.getInstance());
        module.installBuiltin(LEBuiltinFactory.getInstance());
        module.installBuiltin(LTBuiltinFactory.getInstance());
        module.installBuiltin(GEBuiltinFactory.getInstance());
        module.installBuiltin(GTBuiltinFactory.getInstance());
        module.installBuiltin(ConcatBuiltinFactory.getInstance());
        module.installBuiltin(SendBuiltinFactory.getInstance());
        module.installBuiltin(SpawnBuiltinFactory.getInstance());
        registerModule(module);
    }
}
