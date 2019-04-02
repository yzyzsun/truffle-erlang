package me.yzyzsun.jiro.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

public abstract class ExpressionNode extends Node {
    public abstract Object executeGeneric(VirtualFrame frame);
}
