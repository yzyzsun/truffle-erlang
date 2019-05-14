package me.yzyzsun.jiro.runtime;

import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.Jiro;

import java.util.Arrays;

public class JiroBinary implements JiroObject {
    private final byte[] value;

    public JiroBinary(byte[] value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JiroBinary)) return false;
        return Arrays.equals(value, ((JiroBinary) obj).value);
    }

    @Override
    public int compareTo(Object obj) {
        if (obj instanceof JiroBinary) {
            val binaryValue = ((JiroBinary) obj).value;
            if (value.length != binaryValue.length) return Integer.compare(value.length, binaryValue.length);
            for (var i = 0; i < value.length; ++i) {
                val cmp = Jiro.compare(value[i], binaryValue[i]);
                if (cmp != 0) return cmp;
            }
            return 0;
        }
        if (obj instanceof JiroObject) return 1;
        throw new ClassCastException();
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
