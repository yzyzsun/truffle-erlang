package me.yzyzsun.jiro.runtime;

import lombok.val;
import lombok.var;

import java.util.List;

public class JiroList extends JiroObject {
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
