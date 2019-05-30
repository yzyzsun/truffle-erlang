package me.yzyzsun.jiro.runtime;

import lombok.Getter;
import lombok.val;

public class JiroFunctionName {
    @Getter private final String identifier;
    @Getter private final int arity;

    public static JiroFunctionName anonymous(int arity) {
        return new JiroFunctionName("<ANONYMOUS>", arity);
    }

    public JiroFunctionName(String identifier, int arity) {
        this.identifier = identifier;
        this.arity = arity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof JiroFunctionName)) return false;
        val functionName = (JiroFunctionName) obj;
        return identifier.equals(functionName.identifier) && arity == functionName.arity;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode() * 11 + arity;
    }

    @Override
    public String toString() {
        return identifier + "/" + arity;
    }
}
