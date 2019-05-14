package me.yzyzsun.jiro;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.source.SourceSection;
import lombok.val;
import me.yzyzsun.jiro.nodes.EvalRootNode;
import me.yzyzsun.jiro.parser.JiroParser;
import me.yzyzsun.jiro.runtime.*;

import java.math.BigDecimal;

@TruffleLanguage.Registration(id = "jiro", name = "Core Erlang", implementationName = "Jiro")
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
    protected CallTarget parse(ParsingRequest request) {
        val source = request.getSource();
        val module = JiroParser.parse(this, source).getModule();
        val main = module.getFunction(new JiroFunctionName("main", 0), false);
        if (main == null) throw new JiroException("main/0 function not found", null);
        val node = new EvalRootNode(this, main.getCallTarget(), module);
        return Truffle.getRuntime().createCallTarget(node);
    }

    @Override
    protected SourceSection findSourceLocation(JiroContext context, Object value) {
        if (value instanceof JiroFunction) return ((JiroFunction) value).getDeclaredLocation();
        return null;
    }

    public static boolean exactlyEqual(Object left, Object right) {
        if (left == right) return true;
        return left.equals(right);
    }

    public static int compare(Object left, Object right) {
        if (left == right) return 0;
        if (left instanceof Long) {
            if (right instanceof Long) return Long.compare((long) left, (long) right);
            if (right instanceof Double) return Double.compare(((Long) left).doubleValue(), (double) right);
            if (right instanceof JiroBigInteger) return new JiroBigInteger((long) left).compareTo(right);
            if (right instanceof String || right instanceof JiroObject) return -1;
            throw new ClassCastException();
        }
        if (left instanceof Double) {
            if (right instanceof Long) return Double.compare((double) left, ((Long) right).doubleValue());
            if (right instanceof Double) return Double.compare((double) left, (double) right);
            if (right instanceof JiroBigInteger) {
                val bigDecimal = new BigDecimal(((JiroBigInteger) right).getValue());
                return new BigDecimal((double) left).compareTo(bigDecimal);
            }
            if (right instanceof String || right instanceof JiroObject) return -1;
            throw new ClassCastException();
        }
        if (left instanceof String && right instanceof String) return ((String) left).compareTo((String) right);
        if (left instanceof JiroObject) return ((JiroObject) left).compareTo(right);
        throw new ClassCastException();
    }
}
