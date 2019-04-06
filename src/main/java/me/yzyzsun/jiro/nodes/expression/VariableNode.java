package me.yzyzsun.jiro.nodes.expression;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;
import me.yzyzsun.jiro.nodes.ExpressionNode;

@NodeField(name = "slot", type = FrameSlot.class)
public abstract class VariableNode extends ExpressionNode {
    public abstract FrameSlot getSlot();

    @Specialization(guards = "isLong(frame)")
    protected long readLong(VirtualFrame frame) {
        return FrameUtil.getLongSafe(frame, getSlot());
    }

    @Specialization(guards = "isDouble(frame)")
    protected double readDouble(VirtualFrame frame) {
        return FrameUtil.getDoubleSafe(frame, getSlot());
    }

    @Specialization(guards = "isBoolean(frame)")
    protected boolean readBoolean(VirtualFrame frame) {
        return FrameUtil.getBooleanSafe(frame, getSlot());
    }

    @Specialization(replaces = {"readLong", "readDouble", "readBoolean"})
    protected Object readObject(VirtualFrame frame) {
        return FrameUtil.getObjectSafe(frame, getSlot());
    }

    protected boolean isLong(VirtualFrame frame) {
        return frame.getFrameDescriptor().getFrameSlotKind(getSlot()) == FrameSlotKind.Long;
    }

    protected boolean isDouble(VirtualFrame frame) {
        return frame.getFrameDescriptor().getFrameSlotKind(getSlot()) == FrameSlotKind.Double;
    }

    protected boolean isBoolean(VirtualFrame frame) {
        return frame.getFrameDescriptor().getFrameSlotKind(getSlot()) == FrameSlotKind.Boolean;
    }
}
