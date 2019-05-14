package me.yzyzsun.jiro.nodes.pattern;

import com.oracle.truffle.api.frame.VirtualFrame;
import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.runtime.JiroTuple;

public class TuplePatternNode extends PatternNode {
    @Children private PatternNode[] patterns;

    public TuplePatternNode(PatternNode[] patterns) {
        this.patterns = patterns;
    }

    @Override
    public boolean match(Object obj, VirtualFrame frame) {
        if (!(obj instanceof JiroTuple)) return false;
        val tuple = (JiroTuple) obj;
        if (patterns.length != tuple.size()) return false;
        for (var i = 0; i < patterns.length; ++i) {
            if (!patterns[i].match(tuple.get(i), frame)) return false;
        }
        return true;
    }
}
