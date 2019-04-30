package me.yzyzsun.jiro.runtime;

public class JiroCons extends JiroObject {
    private final Object car, cdr;

    public JiroCons(Object car, Object cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    @Override
    public String toString() {
        return "[" + car + " | " + cdr + "]";
    }
}
