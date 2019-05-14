package me.yzyzsun.jiro.runtime;

import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.Jiro;

import java.util.List;

public class JiroList implements JiroObject {
    private final List<?> values;

    public JiroList(List<?> values) {
        this.values = values;
    }

    public int size() {
        return values.size();
    }

    public Object get(int index) {
        return values.get(index);
    }

    public int codePointAt(int index) {
        val value = values.get(index);
        if (!(value instanceof Long)) throw new JiroException("expected string but got list: " + toString(), null);
        return ((Long) value).intValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JiroList)) return false;
        return
                values.equals(((JiroList) obj).values);
    }

    @Override
    public int compareTo(Object obj) {
        if (obj instanceof JiroList) {
            val listValues = ((JiroList) obj).values;
            if (values.size() != listValues.size()) return Integer.compare(values.size(), listValues.size());
            for (var i = 0; i < values.size(); ++i) {
                val cmp = Jiro.compare(values.get(i), listValues.get(i));
                if (cmp != 0) return cmp;
            }
            return 0;
        }
        // TODO: Compare JiroList with JiroCons
        if (obj instanceof JiroBinary) return -1;
        if (obj instanceof JiroObject) return 1;
        throw new ClassCastException();
    }

    @Override
    public String toString() {
        return values.toString();
    }

    public String codePointsToString() {
        val str = new StringBuilder(size() + 2);
        str.append('"');
        for (var i = 0; i < size(); ++i) str.appendCodePoint(codePointAt(i));
        str.append('"');
        return str.toString();
    }
}
