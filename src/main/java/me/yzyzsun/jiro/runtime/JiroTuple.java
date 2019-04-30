package me.yzyzsun.jiro.runtime;

import java.util.List;
import java.util.stream.Collectors;

public class JiroTuple extends JiroObject {
    private final List<Object> values;

    public JiroTuple(List<Object> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "{" + values.stream().map(Object::toString).collect(Collectors.joining(", ")) + "}";
    }
}
