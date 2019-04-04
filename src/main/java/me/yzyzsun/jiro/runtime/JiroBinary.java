package me.yzyzsun.jiro.runtime;

import lombok.val;
import lombok.var;

public class JiroBinary {
    private final byte[] value;

    public JiroBinary(byte[] value) {
        this.value = value;
    }

    @Override
    public String toString() {
        val str = new StringBuilder("#{");
        for (var b : value) str.append("#<").append(b).append(">(), ");
        if (str.length() > 2) str.delete(str.length() - 2, str.length());
        str.append("}#");
        return str.toString();
    }
}
