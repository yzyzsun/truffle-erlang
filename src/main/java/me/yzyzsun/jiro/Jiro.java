package me.yzyzsun.jiro;

import com.oracle.truffle.api.TruffleLanguage;
import me.yzyzsun.jiro.runtime.JiroContext;
import me.yzyzsun.jiro.runtime.JiroObject;

@TruffleLanguage.Registration(id = "jiro", name = "Core Erlang")
public class Jiro extends TruffleLanguage<JiroContext> {
    @Override
    protected JiroContext createContext(Env env) {
        return new JiroContext(this, env);
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        return object instanceof JiroObject;
    }
}
