package me.yzyzsun.jiro;

import lombok.val;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Launcher {
    private static final String JIRO = "jiro";

    public static void main(String[] args) throws IOException {
        Source source;
        if (args.length == 0) {
            source = Source.newBuilder(JIRO, new InputStreamReader(System.in), "<stdin>").build();
        } else {
            val file = args[0];
            source = Source.newBuilder(JIRO, new File(file)).build();
        }
        try (val context = Context.newBuilder(JIRO).build()) {
            val value = context.eval(source);
            System.out.println("main/0 -> " + value);
        } catch (PolyglotException ex) {
            if (ex.isInternalError()) ex.printStackTrace();
            else System.err.println(ex.getMessage());
        }
    }
}
