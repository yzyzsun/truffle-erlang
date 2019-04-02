package me.yzyzsun.jiro.runtime;

public class JiroNil {
    public static final JiroNil SINGLETON = new JiroNil();
    private JiroNil() {}

    @Override
    public String toString() {
        return "[]";
    }
}
