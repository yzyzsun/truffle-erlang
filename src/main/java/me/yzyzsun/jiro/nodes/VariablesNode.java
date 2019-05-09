package me.yzyzsun.jiro.nodes;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.nodes.Node;

public class VariablesNode extends Node {
    public final int length;
    private final FrameSlot[] slots;
    private int count = 0;

    public VariablesNode(int length) {
        this.length = length;
        slots = new FrameSlot[length];
    }

    public FrameSlot get(int index) {
        return slots[index];
    }

    public void add(FrameSlot slot) {
        slots[count++] = slot;
    }
}
