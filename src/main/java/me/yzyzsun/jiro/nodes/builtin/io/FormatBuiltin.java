package me.yzyzsun.jiro.nodes.builtin.io;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.JiroContext;
import me.yzyzsun.jiro.nodes.builtin.BuiltinInfo;
import me.yzyzsun.jiro.nodes.builtin.BuiltinNode;
import me.yzyzsun.jiro.runtime.JiroException;
import me.yzyzsun.jiro.runtime.JiroList;

import java.io.PrintWriter;

@BuiltinInfo(identifier = "format", arity = 2)
public abstract class FormatBuiltin extends BuiltinNode {
    @Specialization
    public String format(JiroList fmt, JiroList data, @CachedContext(Jiro.class) JiroContext context) {
        print(context.getOutput(), fmt, data);
        return "ok";
    }

    @TruffleBoundary
    private void print(PrintWriter out, JiroList fmt, JiroList data) {
        var count = 0;
        val str = new StringBuilder(fmt.size());
        for (var i = 0; i < fmt.size(); ++i) {
            var ch = fmt.codePointAt(i);
            if (ch != '~') {
                str.appendCodePoint(ch);
                continue;
            }
            ch = fmt.codePointAt(++i);
            switch (ch) {
            case '~': str.append('~'); continue;
            case 'n': str.append('\n'); continue;
            }
            Object value;
            try {
                value = data.get(count++);
            } catch (IndexOutOfBoundsException ex) {
                throw JiroException.badArgument("the number of data items is less than that specified in the format string",
                                                this, fmt.codePointsToString(), data);
            }
            switch (ch) {
            case 'c':
                if (!(value instanceof Long)) {
                    throw JiroException.badArgument("the control sequence expected integer",
                                                    this, "~" + (char) ch, value);
                }
                str.appendCodePoint(((Long) value).intValue());
                break;
            case 's':
                if (!(value instanceof JiroList)) {
                    throw JiroException.badArgument("the control sequence expected string",
                                                    this, "~" + (char) ch, value);
                }
                val list = (JiroList) value;
                for (var j = 0; j < list.size(); ++j) str.appendCodePoint(list.codePointAt(j));
                break;
            case 'w':
                str.append(value);
                break;
            case 'f': case 'e': case 'g': case 'p': case 'W': case 'P':
            case 'B': case 'X': case '#': case 'b': case 'x': case '+':
                throw new JiroException("'io:format' currently does not support ~" + (char) ch, this);
            case 'i':
                break;
            }
        }
        if (count != data.size()) {
            throw JiroException.badArgument("the number of data items is more than that specified in the format string",
                                            this, fmt.codePointsToString(), data);
        }
        out.print(str.toString());
        out.flush();
    }

    @Fallback
    public Object badArgument(Object fmt, Object data) {
        throw JiroException.badArgument("expected format string, data list", this, fmt, data);
    }
}
