package me.yzyzsun.jiro.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import lombok.Getter;

import java.math.BigInteger;

@ExportLibrary(InteropLibrary.class)
public class JiroBigInteger extends JiroObject {

    @Getter private BigInteger value;

    public JiroBigInteger(BigInteger value) {
        this.value = value;
    }

    public JiroBigInteger(long value) {
        this.value = BigInteger.valueOf(value);
    }

    @Override @TruffleBoundary
    public boolean equals(Object obj) {
        if (!(obj instanceof JiroBigInteger)) return false;
        return value.equals(((JiroBigInteger) obj).value);
    }

    @TruffleBoundary
    public int compareTo(JiroBigInteger val) {
        return value.compareTo(val.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override @TruffleBoundary
    public String toString() {
        return value.toString();
    }

    private static final long LONG_MAX_SAFE_DOUBLE = 9007199254740991L; // 2^53 - 1
    private static final int INT_MAX_SAFE_FLOAT = 16777215; // 2^24 - 1

    private static boolean inSafeDoubleRange(long l) {
        return -LONG_MAX_SAFE_DOUBLE <= l && l <= LONG_MAX_SAFE_DOUBLE;
    }

    private static boolean inSafeFloatRange(int i) {
        return -INT_MAX_SAFE_FLOAT <= i && i <= INT_MAX_SAFE_FLOAT;
    }

    @ExportMessage
    boolean isNumber() {
        return fitsInLong();
    }

    @ExportMessage @TruffleBoundary
    boolean fitsInByte() {
        return value.bitLength() < 8;
    }

    @ExportMessage @TruffleBoundary
    boolean fitsInShort() {
        return value.bitLength() < 16;
    }

    @ExportMessage @TruffleBoundary
    boolean fitsInInt() {
        return value.bitLength() < 32;
    }

    @ExportMessage @TruffleBoundary
    boolean fitsInLong() {
        return value.bitLength() < 64;
    }

    @ExportMessage @TruffleBoundary
    boolean fitsInFloat() {
        return fitsInInt() && inSafeFloatRange(value.intValue());
    }

    @ExportMessage @TruffleBoundary
    boolean fitsInDouble() {
        return fitsInLong() && inSafeDoubleRange(value.longValue());
    }

    @ExportMessage @TruffleBoundary
    byte asByte() throws UnsupportedMessageException {
        if (fitsInByte()) return value.byteValue();
        else throw UnsupportedMessageException.create();
    }

    @ExportMessage @TruffleBoundary
    short asShort() throws UnsupportedMessageException {
        if (fitsInShort()) return value.shortValue();
        else throw UnsupportedMessageException.create();
    }

    @ExportMessage @TruffleBoundary
    int asInt() throws UnsupportedMessageException {
        if (fitsInInt()) return value.intValue();
        else throw UnsupportedMessageException.create();
    }

    @ExportMessage @TruffleBoundary
    long asLong() throws UnsupportedMessageException {
        if (fitsInLong()) return value.longValue();
        else throw UnsupportedMessageException.create();
    }

    @ExportMessage @TruffleBoundary
    float asFloat() throws UnsupportedMessageException {
        if (fitsInFloat()) return value.floatValue();
        else throw UnsupportedMessageException.create();
    }

    @ExportMessage @TruffleBoundary
    double asDouble() throws UnsupportedMessageException {
        if (fitsInDouble()) return value.doubleValue();
        else throw UnsupportedMessageException.create();
    }
}
