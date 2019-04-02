package me.yzyzsun.jiro.parser;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.nodes.literal.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class ParseListener extends CoreErlangBaseListener {
    private Source source;
    private ParseTreeProperty<Node> values = new ParseTreeProperty<>();

    private static boolean isOctal(char ch) {
        return '0' <= ch && ch <= '7';
    }

    private static String unescape(String oldString) {
        val newString = new StringBuilder(oldString.length());
        for (var i = 0; i < oldString.length(); ++i) {
            var ch = oldString.charAt(i);
            if (ch != '\\') {
                newString.append(ch);
                continue;
            }
            ch = oldString.charAt(++i);
            switch (ch) {
            case 'b': newString.append('\b'); break;
            case 'd': newString.append('\u007f'); break;
            case 'e': newString.append('\u001b'); break;
            case 'f': newString.append('\f'); break;
            case 'n': newString.append('\n'); break;
            case 'r': newString.append('\r'); break;
            case 's': newString.append(' '); break;
            case 't': newString.append('\t'); break;
            case 'v': newString.append('\u000b'); break;
            case '"': newString.append('"'); break;
            case '\'': newString.append('\''); break;
            case '\\': newString.append('\\'); break;
            case '^':
                if (i + 1 >= oldString.length()) return null;
                ch = oldString.charAt(++i);
                if (ch < 64 || ch > 95) return null;
                newString.appendCodePoint(ch - 64);
                break;
            default:
                if (!isOctal(ch)) return null;
                if (i + 1 >= oldString.length() || !isOctal(oldString.charAt(i + 1))) {
                    newString.appendCodePoint(ch - '0');
                } else if (i + 2 >= oldString.length() || !isOctal(oldString.charAt(i + 2))) {
                    newString.appendCodePoint((ch - '0') * 8 + oldString.charAt(i + 1) - '0');
                    ++i;
                } else {
                    newString.appendCodePoint(Integer.parseInt(oldString.substring(i, i + 3), 8));
                    i += 2;
                }
            }
        }
        return newString.toString();
    }

    private void throwParseError(Token token, String message) {
        val line = token.getLine();
        val column = token.getCharPositionInLine();
        val msg = "line " + line + ":" + column + " " + message;
        throw new ParseError(source, line, column, token.getStopIndex() - token.getStartIndex() + 1, msg);
    }

    private void throwInvalidEscapeSequenceError(Token token) {
        throwParseError(token, "invalid escape sequence at: " + token.getText());
    }

    @Override
    public void enterInteger(CoreErlangParser.IntegerContext ctx) {
        values.put(ctx, new IntegerNode(Long.parseLong(ctx.INTEGER().getText())));
    }

    @Override
    public void enterFloat(CoreErlangParser.FloatContext ctx) {
        values.put(ctx, new FloatNode(Double.parseDouble(ctx.FLOAT().getText())));
    }

    @Override
    public void enterChar(CoreErlangParser.CharContext ctx) {
        val str = unescape(ctx.CHAR().getText().substring(1));
        if (str == null) throwInvalidEscapeSequenceError(ctx.getStart());
        values.put(ctx, new IntegerNode(str.codePointAt(0)));
    }

    @Override
    public void enterString(CoreErlangParser.StringContext ctx) {
        val text = ctx.STRING().getText();
        val str = unescape(text.substring(1, text.length() - 1));
        if (str == null) throwInvalidEscapeSequenceError(ctx.getStart());
        values.put(ctx, new StringNode(str));
    }

    @Override
    public void enterAtom(CoreErlangParser.AtomContext ctx) {
        val text = ctx.ATOM().getText();
        val str = unescape(text.substring(1, text.length() - 1));
        if (str == null) throwInvalidEscapeSequenceError(ctx.getStart());
        else if (str.equals("true")) values.put(ctx, new BooleanNode(true));
        else if (str.equals("false")) values.put(ctx, new BooleanNode(false));
        else values.put(ctx, new AtomNode(str));
    }

    @Override
    public void enterNil(CoreErlangParser.NilContext ctx) {
        values.put(ctx, new NilNode());
    }
}
