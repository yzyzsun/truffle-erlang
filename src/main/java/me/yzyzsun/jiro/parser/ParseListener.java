package me.yzyzsun.jiro.parser;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.nodes.expression.*;
import me.yzyzsun.jiro.nodes.literal.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.stream.Collectors;

public class ParseListener extends CoreErlangBaseListener {
    private Source source;
    private FrameDescriptor frameDescriptor;
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
    public void exitExpression(CoreErlangParser.ExpressionContext ctx) {
        if (ctx.singleExpression().size() == 1) {
            values.put(ctx, values.get(ctx.singleExpression(0)));
        } else {
            val nodes = ctx.singleExpression().stream().map(x -> (ExpressionNode) values.get(x)).collect(Collectors.toList());
            values.put(ctx, new SequenceNode(nodes));
        }
    }

    @Override
    public void exitVariable(CoreErlangParser.VariableContext ctx) {
        val slot = frameDescriptor.findFrameSlot(ctx.VARIABLE_NAME().getText());
        if (slot == null) throwParseError(ctx.getStart(), "unbound variable: " + ctx.getText());
        values.put(ctx, VariableNodeGen.create(slot));
    }

    @Override
    public void exitLiteral(CoreErlangParser.LiteralContext ctx) {
        values.put(ctx, values.get(ctx.atomicLiteral()));
    }

    @Override
    public void exitTuple(CoreErlangParser.TupleContext ctx) {
        val nodes = ctx.expression().stream().map(x -> (ExpressionNode) values.get(x)).collect(Collectors.toList());
        values.put(ctx, new TupleNode(nodes));
    }

    @Override
    public void exitList(CoreErlangParser.ListContext ctx) {
        val nodes = ctx.expression().stream().map(x -> (ExpressionNode) values.get(x)).collect(Collectors.toList());
        values.put(ctx, new ListNode(nodes));
    }

    @Override
    public void exitCons(CoreErlangParser.ConsContext ctx) {
        val nodes = ctx.expression().stream().map(x -> (ExpressionNode) values.get(x)).collect(Collectors.toList());
        val last = nodes.get(nodes.size() - 1);
        nodes.remove(nodes.size() - 1);
        if (last instanceof NilNode) {
            values.put(ctx, new ListNode(nodes));
        } else if (last instanceof ListNode) {
            values.put(ctx, new ListNode(nodes, (ListNode) last));
        } else {
            var node = last;
            for (var i = nodes.size() - 1; i >= 0; --i) {
                node = new ConsNode(nodes.get(i), node);
            }
            values.put(ctx, node);
        }
    }

    @Override
    public void exitBinary(CoreErlangParser.BinaryContext ctx) {
        val nodes = ctx.bitstring().stream().map(x -> (ExpressionNode) values.get(x)).collect(Collectors.toList());
        values.put(ctx, new BinaryNode(nodes));
    }

    @Override
    public void exitBitstring(CoreErlangParser.BitstringContext ctx) {
        // Ignore encoding expressions which are implementation-dependent
        values.put(ctx, values.get(ctx.expression(0)));
    }

    @Override
    public void exitInteger(CoreErlangParser.IntegerContext ctx) {
        values.put(ctx, new IntegerNode(Long.parseLong(ctx.INTEGER().getText())));
    }

    @Override
    public void exitFloat(CoreErlangParser.FloatContext ctx) {
        values.put(ctx, new FloatNode(Double.parseDouble(ctx.FLOAT().getText())));
    }

    @Override
    public void exitChar(CoreErlangParser.CharContext ctx) {
        val str = unescape(ctx.CHAR().getText().substring(1));
        if (str == null) throwInvalidEscapeSequenceError(ctx.getStart());
        values.put(ctx, new IntegerNode(str.codePointAt(0)));
    }

    @Override
    public void exitString(CoreErlangParser.StringContext ctx) {
        val text = ctx.STRING().getText();
        val str = unescape(text.substring(1, text.length() - 1));
        if (str == null) throwInvalidEscapeSequenceError(ctx.getStart());
        values.put(ctx, new StringNode(str));
    }

    @Override
    public void exitAtom(CoreErlangParser.AtomContext ctx) {
        val text = ctx.ATOM().getText();
        val str = unescape(text.substring(1, text.length() - 1));
        if (str == null) throwInvalidEscapeSequenceError(ctx.getStart());
        else if (str.equals("true")) values.put(ctx, new BooleanNode(true));
        else if (str.equals("false")) values.put(ctx, new BooleanNode(false));
        else values.put(ctx, new AtomNode(str));
    }

    @Override
    public void exitNil(CoreErlangParser.NilContext ctx) {
        values.put(ctx, new NilNode());
    }
}