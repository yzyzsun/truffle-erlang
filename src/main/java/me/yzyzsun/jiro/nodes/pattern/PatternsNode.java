package me.yzyzsun.jiro.nodes.pattern;

import com.oracle.truffle.api.frame.VirtualFrame;
import lombok.val;
import lombok.var;
import me.yzyzsun.jiro.runtime.JiroSequence;

public class PatternsNode extends PatternNode {
    @Children private PatternNode[] patterns;

    public PatternsNode(PatternNode[] patterns) {
        this.patterns = patterns;
    }

    @Override
    public boolean match(Object obj, VirtualFrame frame) {
        if (!(obj instanceof JiroSequence)) return false;
        val sequence = (JiroSequence) obj;
        if (patterns.length != sequence.size()) return false;
        for (var i = 0; i < patterns.length; ++i) {
            if (!patterns[i].match(sequence.get(i), frame)) return false;
        }
        return true;
    }
}
