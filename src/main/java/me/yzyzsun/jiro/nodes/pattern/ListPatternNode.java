package me.yzyzsun.jiro.nodes.pattern;

import com.oracle.truffle.api.frame.VirtualFrame;
import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.runtime.JiroList;

public class ListPatternNode extends PatternNode {
    @Children private PatternNode[] patterns;

    public ListPatternNode(PatternNode[] patterns) {
        this.patterns = patterns;
    }

    @Override
    public boolean match(Object obj, VirtualFrame frame) {
        if (!(obj instanceof JiroList)) return false;
        val list = (JiroList) obj;
        if (patterns.length != list.size()) return false;
        for (var i = 0; i < patterns.length; ++i) {
            if (!patterns[i].match(list.get(i), frame)) return false;
        }
        return true;
    }
}
