package me.yzyzsun.jiro.runtime;

public class JiroNil implements JiroObject {
    public static final JiroNil SINGLETON = new JiroNil();
    private JiroNil() {}

    @Override
    public int compareTo(Object obj) {
        if (this == obj) return 0;
        if (obj instanceof JiroList || obj instanceof JiroCons || obj instanceof JiroBinary) return -1;
        if (obj instanceof JiroObject) return 1;
        throw new ClassCastException();
    }

    @Override
    public String toString() {
        return "[]";
    }
}
