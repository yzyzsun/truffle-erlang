package me.yzyzsun.jiro.runtime;

import akka.actor.ActorRef;

public class JiroPid implements JiroObject {
    private ActorRef ref;

    public JiroPid(ActorRef ref) {
        this.ref = ref;
    }

    public void send(Object obj) {
        ref.tell(obj, ActorRef.noSender());
    }

    @Override
    public int compareTo(Object obj) {
        if (obj instanceof JiroPid) return ref.compareTo(((JiroPid) obj).ref);
        if (obj instanceof Long || obj instanceof Double || obj instanceof JiroBigInteger
        ||  obj instanceof String || obj instanceof JiroFunction) return 1;
        if (obj instanceof JiroObject) return -1;
        throw new ClassCastException();
    }

    @Override
    public String toString() {
        return ref.toString();
    }
}
