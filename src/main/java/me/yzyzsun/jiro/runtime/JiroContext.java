package me.yzyzsun.jiro.runtime;

import com.oracle.truffle.api.TruffleLanguage;
import lombok.Getter;
import me.yzyzsun.jiro.Jiro;

public class JiroContext {
    private final Jiro language;
    @Getter private final TruffleLanguage.Env env;

    public JiroContext(Jiro language, TruffleLanguage.Env env) {
        this.language = language;
        this.env = env;
    }
}
