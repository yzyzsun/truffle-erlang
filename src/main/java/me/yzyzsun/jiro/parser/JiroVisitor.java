package me.yzyzsun.jiro.parser;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.nodes.ExpressionNode;
import me.yzyzsun.jiro.nodes.JiroRootNode;
import me.yzyzsun.jiro.nodes.ModuleNode;
import me.yzyzsun.jiro.nodes.VariablesNode;
import me.yzyzsun.jiro.nodes.expression.*;
import me.yzyzsun.jiro.nodes.literal.*;
import me.yzyzsun.jiro.nodes.local.BindVariableNode;
import me.yzyzsun.jiro.nodes.local.BindVariableNodeGen;
import me.yzyzsun.jiro.nodes.local.ReadArgumentNode;
import me.yzyzsun.jiro.nodes.local.ReadVariableNodeGen;
import me.yzyzsun.jiro.nodes.pattern.*;
import me.yzyzsun.jiro.runtime.JiroFunction;
import me.yzyzsun.jiro.runtime.JiroFunctionName;
import me.yzyzsun.jiro.runtime.JiroModule;
import org.antlr.v4.runtime.Token;

import java.math.BigInteger;

public class JiroVisitor extends CoreErlangBaseVisitor<Node> {
    private final Jiro language;
    private final Source source;
    private JiroModule currentModule;
    private FrameDescriptor frameDescriptor;

    public JiroVisitor(Jiro language, Source source) {
        this.language = language;
        this.source = source;
    }

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
    public Node visitModule(CoreErlangParser.ModuleContext ctx) {
        val text = ctx.ATOM().getText();
        val moduleName = unescape(text.substring(1, text.length() - 1));
        currentModule = new JiroModule(language, moduleName);
        for (val context : ctx.functionName()) {
            val functionName = ((FunctionNameNode) this.visit(context)).getFunctionName();
            currentModule.export(functionName);
        }
        for (val def : ctx.functionDefinition()) this.visit(def);
        return new ModuleNode(currentModule);
    }

    @Override
    public Node visitInteger(CoreErlangParser.IntegerContext ctx) {
        try {
            return new IntegerNode(Long.parseLong(ctx.INTEGER().getText()));
        } catch (NumberFormatException ex) {
            return new BigIntegerNode(new BigInteger(ctx.INTEGER().getText()));
        }
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

    @Override
    public Node visitFunctionDefinition(CoreErlangParser.FunctionDefinitionContext ctx) {
        frameDescriptor = new FrameDescriptor();
        val functionName = ((FunctionNameNode) this.visit(ctx.functionName())).getFunctionName();
        val root = visitFunRoot(ctx.fun());
        root.setName(functionName.toString());
        currentModule.registerFunction(functionName, Truffle.getRuntime().createCallTarget(root));
        return null;
    }

    @Override
    public Node visitFunctionName(CoreErlangParser.FunctionNameContext ctx) {
        val text = ctx.ATOM().getText();
        val identifier = unescape(text.substring(1, text.length() - 1));
        val arity = Integer.parseInt(ctx.INTEGER().getText());
        return new FunctionNameNode(language, currentModule.getName(), identifier, arity, false);
    }


    private JiroRootNode visitFunRoot(CoreErlangParser.FunContext ctx) {
        val argc = ctx.VARIABLE_NAME().size();
        val bindNodes = new BindVariableNode[argc];
        for (var i = 0; i < argc; ++i) {
            val slot = frameDescriptor.findOrAddFrameSlot(ctx.VARIABLE_NAME(i).getText());
            val value = new ReadArgumentNode(i);
            bindNodes[i] = BindVariableNodeGen.create(value, slot);
        }
        val exprNode = (ExpressionNode) this.visit(ctx.expression());
        exprNode.markAsTail();
        val letNode = new LetNode(bindNodes, exprNode);
        val startIndex = ctx.start.getStartIndex();
        val sourceSection = source.createSection(startIndex, ctx.stop.getStopIndex() - startIndex);
        return new JiroRootNode(language, frameDescriptor, letNode, sourceSection);
    }

    @Override
    public Node visitFun(CoreErlangParser.FunContext ctx) {
        val rootNode = visitFunRoot(ctx);
        val function = new JiroFunction(language, JiroFunctionName.anonymous(ctx.VARIABLE_NAME().size()));
        function.setCallTarget(Truffle.getRuntime().createCallTarget(rootNode));
        return new FunNode(function);
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
    public Node visitFname(CoreErlangParser.FnameContext ctx) {
        return this.visit(ctx.functionName());
    }

    @Override
    public Node visitF(CoreErlangParser.FContext ctx) {
        return this.visit(ctx.fun());
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
        // TODO: Process encoding parameters which are implementation-dependent
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
    public Node visitCase(CoreErlangParser.CaseContext ctx) {
        val expression = (ExpressionNode) this.visit(ctx.expression());
        val clauses = ctx.clause().stream().map(this::visit).toArray(ClauseNode[]::new);
        return new CaseNode(expression, clauses);
    }

    @Override
    public Node visitApplication(CoreErlangParser.ApplicationContext ctx) {
        val function = (ExpressionNode) this.visit(ctx.expression(0));
        val arguments = ctx.expression().stream().skip(1).map(this::visit).toArray(ExpressionNode[]::new);
        return new InvokeNode(function, arguments);
    }

    @Override
    public Node visitInterModuleCall(CoreErlangParser.InterModuleCallContext ctx) {
        val moduleAtom = this.visit(ctx.expression(0));
        val functionAtom = this.visit(ctx.expression(1));
        if (!(moduleAtom instanceof AtomNode && functionAtom instanceof AtomNode)) {
            throwParseError(ctx.start, "m and f in 'call m:f(...)' should both evaluate to atoms");
        }
        val moduleName = ((AtomNode) moduleAtom).getValue();
        val identifier = ((AtomNode) functionAtom).getValue();
        val functionName = new FunctionNameNode(language, moduleName, identifier, ctx.expression().size() - 2, true);
        val arguments = ctx.expression().stream().skip(2).map(this::visit).toArray(ExpressionNode[]::new);
        return new InvokeNode(functionName, arguments);
    }

    @Override
    public Node visitSequencing(CoreErlangParser.SequencingContext ctx) {
        return new SequencingNode((ExpressionNode) this.visit(ctx.expression(0)),
                                  (ExpressionNode) this.visit(ctx.expression(1)));
    }

    @Override
    public Node visitVariables(CoreErlangParser.VariablesContext ctx) {
        val node = new VariablesNode(ctx.VARIABLE_NAME().size());
        for (val variable : ctx.VARIABLE_NAME()) {
            node.add(frameDescriptor.findOrAddFrameSlot(variable.getText()));
        }
        return node;
    }

    @Override
    public Node visitClause(CoreErlangParser.ClauseContext ctx) {
        val pattern = (PatternNode) this.visit(ctx.patterns());
        val guard = (ExpressionNode) this.visit(ctx.expression(0));
        val expression = (ExpressionNode) this.visit(ctx.expression(1));
        return new ClauseNode(pattern, guard, expression);
    }

    @Override
    public Node visitPatterns(CoreErlangParser.PatternsContext ctx) {
        if (ctx.pattern().size() == 1) {
            return this.visit(ctx.pattern(0));
        } else {
            val patterns = ctx.pattern().stream().map(this::visit).toArray(PatternNode[]::new);
            return new PatternsNode(patterns);
        }
    }

    @Override
    public Node visitVariablePattern(CoreErlangParser.VariablePatternContext ctx) {
        val slot = frameDescriptor.findOrAddFrameSlot(ctx.VARIABLE_NAME().getText());
        return new VariablePatternNode(slot);
    }

    @Override
    public Node visitLiteralPattern(CoreErlangParser.LiteralPatternContext ctx) {
        val literal = (ExpressionNode) this.visit(ctx.atomicLiteral());
        return new LiteralPatternNode(literal);
    }

    @Override
    public Node visitTuplePattern(CoreErlangParser.TuplePatternContext ctx) {
        val patterns = ctx.pattern().stream().map(this::visit).toArray(PatternNode[]::new);
        return new TuplePatternNode(patterns);
    }

    @Override
    public Node visitListPattern(CoreErlangParser.ListPatternContext ctx) {
        val patterns = ctx.pattern().stream().map(this::visit).toArray(PatternNode[]::new);
        return new ListPatternNode(patterns);
    }

    @Override
    public Node visitAliasPattern(CoreErlangParser.AliasPatternContext ctx) {
        val slot = frameDescriptor.findOrAddFrameSlot(ctx.VARIABLE_NAME().getText());
        val pattern = (PatternNode) this.visit(ctx.pattern());
        return new AliasPatternNode(slot, pattern);
    }
}
