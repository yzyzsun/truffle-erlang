package me.yzyzsun.jiro.parser;

import com.oracle.truffle.api.source.Source;
import lombok.val;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.nodes.ModuleNode;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class JiroParser {
    public static ModuleNode parse(Jiro language, Source source) {
        val input = CharStreams.fromString(source.getCharacters().toString());
        val lexer = new CoreErlangLexer(input);
        val tokens = new CommonTokenStream(lexer);
        val parser = new CoreErlangParser(tokens);
        val visitor = new JiroVisitor(language, source);
        return (ModuleNode) visitor.visit(parser.module());
    }
}
