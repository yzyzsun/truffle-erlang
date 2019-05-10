package me.yzyzsun.jiro.runtime;

import lombok.val;

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
        if (!(value instanceof Long)) throw new JiroException("this list is not a string: " + toString(), null);
        return ((Long) value).intValue();
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
