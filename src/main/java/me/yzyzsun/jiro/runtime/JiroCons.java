package me.yzyzsun.jiro.runtime;

import lombok.Getter;
import lombok.val;
import me.yzyzsun.jiro.Jiro;

public class JiroCons implements JiroObject {
    @Getter private final Object car, cdr;

    public JiroCons(Object car, Object cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JiroCons)) return false;
        val cons = (JiroCons) obj;
        return car.equals(cons.car) && cdr.equals(cons.cdr);
    }

    @Override
    public int compareTo(Object obj) {
        if (obj instanceof JiroCons) {
            val cons = (JiroCons) obj;
            val cmp = Jiro.compare(car, cons.car);
            if (cmp != 0) return cmp;
            return Jiro.compare(cdr, cons.cdr);
        }
        // TODO: Compare JiroCons with JiroList
        if (obj instanceof JiroBinary) return -1;
        if (obj instanceof JiroObject) return 1;
        throw new ClassCastException();
    }

    @Override
    public String toString() {
        return "[" + car + " | " + cdr + "]";
    }
}
