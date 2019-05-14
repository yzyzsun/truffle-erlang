package me.yzyzsun.jiro.runtime;

import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.Jiro;

import java.util.List;
import java.util.stream.Collectors;

public class JiroTuple implements JiroObject {
    private final List<?> values;

    public JiroTuple(List<Object> values) {
        this.values = values;
    }

    public int size() {
        return values.size();
    }

    public Object get(int index) {
        return values.get(index);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JiroTuple)) return false;
        return values.equals(((JiroTuple) obj).values);
    }

    @Override
    public int compareTo(Object obj) {
        if (obj instanceof JiroTuple) {
            val tupleValues = ((JiroTuple) obj).values;
            if (values.size() != tupleValues.size()) return Integer.compare(values.size(), tupleValues.size());
            for (var i = 0; i < values.size(); ++i) {
                val cmp = Jiro.compare(values.get(i), tupleValues.get(i));
                if (cmp != 0) return cmp;
            }
            return 0;
        }
        if (obj instanceof JiroNil || obj instanceof JiroList || obj instanceof JiroCons || obj instanceof JiroBinary) return -1;
        if (obj instanceof JiroObject) return 1;
        throw new ClassCastException();
    }

    @Override
    public String toString() {
        return "{" + values.stream().map(Object::toString).collect(Collectors.joining(", ")) + "}";
    }
}
