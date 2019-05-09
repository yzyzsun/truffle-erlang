package me.yzyzsun.jiro;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import lombok.val;
import me.yzyzsun.jiro.nodes.EvalRootNode;
import me.yzyzsun.jiro.parser.JiroParser;
import me.yzyzsun.jiro.runtime.JiroContext;
import me.yzyzsun.jiro.runtime.JiroException;
import me.yzyzsun.jiro.runtime.JiroFunctionName;
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

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        val source = request.getSource();
        val module = JiroParser.parse(this, source).getModule();
        val main = module.getFunction(new JiroFunctionName("main", 0), false);
        if (main == null) throw new JiroException("main/0 function not found", null);
        val node = new EvalRootNode(this, main.getCallTarget(), module);
        return Truffle.getRuntime().createCallTarget(node);
    }
}
