package me.yzyzsun.jiro.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;

import java.util.Collections;

@TypeSystem({long.class, JiroBigInteger.class, double.class, boolean.class, String.class, JiroTuple.class,
             JiroList.class, JiroCons.class, JiroBinary.class, JiroNil.class})
public abstract class JiroTypes {
    @TypeCheck(JiroNil.class)
    public static boolean isJiroNil(Object value) {
        return value == JiroNil.SINGLETON;
    }

    @TypeCast(JiroNil.class)
    public static JiroNil asJiroNil(Object value) {
        assert isJiroNil(value);
        return JiroNil.SINGLETON;
    }

    @ImplicitCast @TruffleBoundary
    public static JiroList castList(JiroNil value) {
        return new JiroList(Collections.emptyList());
    }

    @ImplicitCast @TruffleBoundary
    public static JiroBigInteger castBigInteger(long value) {
        return new JiroBigInteger(value);
    }

    @ImplicitCast
    public static double castDouble(long value) {
        return value;
    }

    @ImplicitCast
    public static double castDouble(JiroBigInteger value) {
        return value.getValue().doubleValue();
    }
}
