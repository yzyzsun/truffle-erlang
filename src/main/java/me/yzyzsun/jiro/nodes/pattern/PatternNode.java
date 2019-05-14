package me.yzyzsun.jiro.nodes.pattern;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

public abstract class PatternNode extends Node {
    public abstract boolean match(Object obj, VirtualFrame frame);
}
