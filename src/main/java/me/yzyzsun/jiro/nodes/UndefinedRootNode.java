package me.yzyzsun.jiro.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import me.yzyzsun.jiro.Jiro;
import me.yzyzsun.jiro.runtime.JiroException;

public class UndefinedRootNode extends JiroRootNode {
    public UndefinedRootNode(Jiro language, String name) {
        super(language, null, null, null, name);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        throw new JiroException("undefined function: " + getName(), null);
    }
}
