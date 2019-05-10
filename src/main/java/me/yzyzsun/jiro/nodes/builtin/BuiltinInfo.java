package me.yzyzsun.jiro.nodes.builtin;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface BuiltinInfo {
    String identifier();
    int arity();
}
