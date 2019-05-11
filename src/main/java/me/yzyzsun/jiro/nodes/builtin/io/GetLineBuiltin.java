package me.yzyzsun.jiro.nodes.builtin.io;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;
import me.yzyzsun.jiro.JiroContext;
import me.yzyzsun.jiro.runtime.JiroException;
import me.yzyzsun.jiro.runtime.JiroList;
import me.yzyzsun.jiro.runtime.JiroNil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@BuiltinInfo(identifier = "get_line", arity = 1)
public abstract class GetLineBuiltin extends BuiltinNode {
    @Specialization
    public JiroList getLine(JiroNil prompt, @CachedContext(Jiro.class) JiroContext context) {
        return readLine(context.getInput());
    }

    @Specialization
    public JiroList getLine(JiroList prompt, @CachedContext(Jiro.class) JiroContext context) {
        print(prompt, context.getOutput());
        return readLine(context.getInput());
    }

    @TruffleBoundary
    private JiroList readLine(BufferedReader in) {
        try {
            val str = in.readLine();
            if (str == null) throw new JiroException("end of input stream reached", this);
            return new JiroList(str.codePoints().asLongStream().boxed().collect(Collectors.toList()));
        } catch (IOException ex) {
            throw new JiroException(ex.getMessage(), this);
        }
    }

    @TruffleBoundary
    private void print(JiroList prompt, PrintWriter out) {
        val str = new StringBuilder(prompt.size());
        for (var i = 0; i < prompt.size(); ++i) str.appendCodePoint(prompt.codePointAt(i));
        out.print(str.toString());
        out.flush();
    }

    @Fallback
    public Object badArgument(Object prompt) {
        throw JiroException.badArgument("expected prompt string", this, prompt);
    }
}
