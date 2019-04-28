package me.yzyzsun.jiro.nodes.local;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import lombok.val;
import me.yzyzsun.jiro.nodes.ExpressionNode;

@NodeChild("valueNode")
@NodeField(name = "slot", type = FrameSlot.class)
public abstract class BindVariableNode extends ExpressionNode {
    protected abstract FrameSlot getSlot();

    @Specialization(guards = "isLongOrIllegal(frame)")
    protected long bindLong(VirtualFrame frame, long value) {
        frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Long);
        frame.setLong(getSlot(), value);
        return value;
    }

    @Specialization(guards = "isDoubleOrIllegal(frame)")
    protected double bindDouble(VirtualFrame frame, double value) {
        frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Double);
        frame.setDouble(getSlot(), value);
        return value;
    }

    @Specialization(guards = "isBooleanOrIllegal(frame)")
    protected boolean bindBoolean(VirtualFrame frame, boolean value) {
        frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Boolean);
        frame.setBoolean(getSlot(), value);
        return value;
    }

    @Specialization(replaces = {"bindLong", "bindDouble", "bindBoolean"})
    protected Object bindObject(VirtualFrame frame, Object value) {
        frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Object);
        frame.setObject(getSlot(), value);
        return value;
    }

    protected boolean isLongOrIllegal(VirtualFrame frame) {
        val kind = frame.getFrameDescriptor().getFrameSlotKind(getSlot());
        return kind == FrameSlotKind.Long || kind == FrameSlotKind.Illegal;
    }

    protected boolean isDoubleOrIllegal(VirtualFrame frame) {
        val kind = frame.getFrameDescriptor().getFrameSlotKind(getSlot());
        return kind == FrameSlotKind.Double || kind == FrameSlotKind.Illegal;
    }

    protected boolean isBooleanOrIllegal(VirtualFrame frame) {
        val kind = frame.getFrameDescriptor().getFrameSlotKind(getSlot());
        return kind == FrameSlotKind.Boolean || kind == FrameSlotKind.Illegal;
    }
}
