package me.yzyzsun.jiro.runtime;

import java.util.List;

public class JiroList extends JiroObject {
    private final List<?> values;

    public JiroList(List<?> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
