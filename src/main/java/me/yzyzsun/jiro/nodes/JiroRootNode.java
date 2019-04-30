package me.yzyzsun.jiro.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;
import lombok.Getter;
import me.yzyzsun.jiro.Jiro;

public class JiroRootNode extends RootNode {
    @Child private ExpressionNode bodyNode;
    @Getter private final SourceSection sourceSection;
    @Getter private final String name;

    public JiroRootNode(Jiro language, FrameDescriptor frameDescriptor, ExpressionNode bodyNode,
                        SourceSection sourceSection, String name) {
        super(language, frameDescriptor);
        this.bodyNode = bodyNode;
        this.sourceSection = sourceSection;
        this.name = name;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return bodyNode.executeGeneric(frame);
    }

    @Override
    public String toString() {
        return "root " + name;
    }
}
