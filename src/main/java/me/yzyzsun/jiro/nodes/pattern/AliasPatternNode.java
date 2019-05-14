package me.yzyzsun.jiro.nodes.pattern;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

public class AliasPatternNode extends PatternNode {
    @Child private VariablePatternNode alias;
    @Child private PatternNode pattern;

    public AliasPatternNode(FrameSlot slot, PatternNode pattern) {
        this.alias = new VariablePatternNode(slot);
        this.pattern = pattern;
    }

    @Override
    public boolean match(Object obj, VirtualFrame frame) {
        if (!pattern.match(obj, frame)) return false;
        return alias.match(obj, frame);
    }
}
