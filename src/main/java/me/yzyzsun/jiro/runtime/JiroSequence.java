package me.yzyzsun.jiro.runtime;

import java.util.List;
import java.util.stream.Collectors;

public class JiroSequence extends JiroObject {
    private final List<Object> values;

    public JiroSequence(List<Object> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "<" + values.stream().map(Object::toString).collect(Collectors.joining(", ")) + ">";
    }
}
