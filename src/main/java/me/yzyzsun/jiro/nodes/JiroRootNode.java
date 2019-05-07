package me.yzyzsun.jiro.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;
import lombok.Getter;
import lombok.Setter;
import me.yzyzsun.jiro.Jiro;

public class JiroRootNode extends RootNode {
    @Child private ExpressionNode bodyNode;
    @Getter private final SourceSection sourceSection;
    @Getter @Setter private String name = "<ANONYMOUS>";

    public JiroRootNode(Jiro language, FrameDescriptor frameDescriptor, ExpressionNode bodyNode, SourceSection sourceSection) {
        super(language, frameDescriptor);
        this.bodyNode = bodyNode;
        this.sourceSection = sourceSection;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return bodyNode.executeGeneric(frame);
    }

    @Override
    public String toString() {
        return "root@" + name;
    }
}
