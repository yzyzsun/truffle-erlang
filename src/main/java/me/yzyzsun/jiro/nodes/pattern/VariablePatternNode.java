package me.yzyzsun.jiro.nodes.pattern;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;

public class VariablePatternNode extends PatternNode {
    private FrameSlot slot;

    public VariablePatternNode(FrameSlot slot) {
        this.slot = slot;
    }

    @Override
    public boolean match(Object obj, VirtualFrame frame) {
        // TODO: Do specialization like BindVariableNode
        frame.getFrameDescriptor().setFrameSlotKind(slot, FrameSlotKind.Object);
        frame.setObject(slot, obj);
        return true;
    }
}
