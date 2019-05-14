package me.yzyzsun.jiro.runtime;

import java.util.List;
import java.util.stream.Collectors;

public class JiroSequence {
    private final List<Object> values;

    public JiroSequence(List<Object> values) {
        this.values = values;
    }

    public int size() {
        return values.size();
    }

    public Object get(int index) {
        return values.get(index);
    }

    @Override
    public String toString() {
        return "<" + values.stream().map(Object::toString).collect(Collectors.joining(", ")) + ">";
    }
}
