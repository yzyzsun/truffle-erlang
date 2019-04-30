package me.yzyzsun.jiro.parser;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.nodes.expression.*;
import me.yzyzsun.jiro.nodes.literal.*;
import me.yzyzsun.jiro.nodes.local.BindVariableNode;
import me.yzyzsun.jiro.nodes.local.BindVariableNodeGen;
import me.yzyzsun.jiro.nodes.local.ReadVariableNodeGen;
import org.antlr.v4.runtime.Token;

public class JiroVisitor extends CoreErlangBaseVisitor<Node> {
    private Source source;
    private FrameDescriptor frameDescriptor;

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
    public Node visitExpression(CoreErlangParser.ExpressionContext ctx) {
        if (ctx.singleExpression().size() == 1) {
            return this.visit(ctx.singleExpression(0));
        } else {
            val nodes = ctx.singleExpression().stream().map(this::visit).toArray(ExpressionNode[]::new);
            return new SequenceNode(nodes);
        }
    }

    @Override
    public Node visitVariable(CoreErlangParser.VariableContext ctx) {
        val slot = frameDescriptor.findFrameSlot(ctx.VARIABLE_NAME().getText());
        if (slot == null) throwParseError(ctx.getStart(), "unbound variable: " + ctx.getText());
        return ReadVariableNodeGen.create(slot);
    }

    @Override
    public Node visitLiteral(CoreErlangParser.LiteralContext ctx) {
        return this.visit(ctx.atomicLiteral());
    }

    @Override
    public Node visitTuple(CoreErlangParser.TupleContext ctx) {
        val nodes = ctx.expression().stream().map(this::visit).toArray(ExpressionNode[]::new);
        return new TupleNode(nodes);
    }

    @Override
    public Node visitList(CoreErlangParser.ListContext ctx) {
        val nodes = ctx.expression().stream().map(this::visit).toArray(ExpressionNode[]::new);
        return new ListNode(nodes);
    }

    @Override
    public Node visitCons(CoreErlangParser.ConsContext ctx) {
        val exps = ctx.expression();
        val front = exps.stream().limit(exps.size() - 1).map(this::visit).toArray(ExpressionNode[]::new);
        val last = (ExpressionNode) this.visit(exps.get(exps.size() - 1));
        if (last instanceof NilNode) {
            return new ListNode(front);
        } else if (last instanceof ListNode) {
            return new ListNode(front, (ListNode) last);
        } else {
            var node = last;
            for (var i = front.length - 1; i >= 0; --i) {
                node = new ConsNode(front[i], node);
            }
            return node;
        }
    }

    @Override
    public Node visitBinary(CoreErlangParser.BinaryContext ctx) {
        val nodes = ctx.bitstring().stream().map(this::visit).toArray(ExpressionNode[]::new);
        return new BinaryNode(nodes);
    }

    @Override
    public Node visitBitstring(CoreErlangParser.BitstringContext ctx) {
        // Ignore encoding expressions which are implementation-dependent
        return this.visit(ctx.expression(0));
    }

    @Override
    public Node visitLet(CoreErlangParser.LetContext ctx) {
        val variables = (VariablesNode) this.visit(ctx.variables());
        val binding = (ExpressionNode) this.visit(ctx.expression(0));
        val expr = (ExpressionNode) this.visit(ctx.expression(1));
        if (binding instanceof SequenceNode) {
            val sequence = (SequenceNode) binding;
            if (variables.length != sequence.length) throwParseError(ctx.start, "the number of variables is "
                    + variables.length + ", but the degree of the binding sequence is " + sequence.length);
            val bindNodes = new BindVariableNode[sequence.length];
            for (var i = 0; i < sequence.length; ++i) {
                bindNodes[i] = BindVariableNodeGen.create(sequence.get(i), variables.get(i));
            }
            return new LetNode(bindNodes, expr);
        } else {
            if (variables.length != 1) throwParseError(ctx.start, "a single expression cannot be bound to "
                    + variables.length + " variables");
            val bindNode = new BindVariableNode[1];
            bindNode[0] = BindVariableNodeGen.create(binding, variables.get(0));
            return new LetNode(bindNode, expr);
        }
    }

    @Override
    public Node visitVariables(CoreErlangParser.VariablesContext ctx) {
        val node = new VariablesNode(ctx.VARIABLE_NAME().size());
        for (val variable : ctx.VARIABLE_NAME()) {
            node.add(frameDescriptor.addFrameSlot(variable.getText()));
        }
        return node;
    }

    @Override
    public Node visitInteger(CoreErlangParser.IntegerContext ctx) {
        return new IntegerNode(Long.parseLong(ctx.INTEGER().getText()));
    }

    @Override
    public Node visitFloat(CoreErlangParser.FloatContext ctx) {
        return new FloatNode(Double.parseDouble(ctx.FLOAT().getText()));
    }

    @Override
    public Node visitChar(CoreErlangParser.CharContext ctx) {
        val str = unescape(ctx.CHAR().getText().substring(1));
        if (str == null) throwInvalidEscapeSequenceError(ctx.getStart());
        return new IntegerNode(str.codePointAt(0));
    }

    @Override
    public Node visitString(CoreErlangParser.StringContext ctx) {
        val text = ctx.STRING().getText();
        val str = unescape(text.substring(1, text.length() - 1));
        if (str == null) throwInvalidEscapeSequenceError(ctx.getStart());
        return new StringNode(str);
    }

    @Override
    public Node visitAtom(CoreErlangParser.AtomContext ctx) {
        val text = ctx.ATOM().getText();
        val str = unescape(text.substring(1, text.length() - 1));
        if (str == null) throwInvalidEscapeSequenceError(ctx.getStart());
        else if (str.equals("true")) return new BooleanNode(true);
        else if (str.equals("false")) return new BooleanNode(false);
        return new AtomNode(str);
    }

    @Override
    public Node visitNil(CoreErlangParser.NilContext ctx) {
        return new NilNode();
    }
}
