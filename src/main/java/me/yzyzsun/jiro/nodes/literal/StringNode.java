package me.yzyzsun.jiro.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import me.yzyzsun.jiro.nodes.ExpressionNode;

import java.util.List;
import java.util.stream.Collectors;

public class StringNode extends ExpressionNode {
    private final List<Long> value;

    public StringNode(String value) {
        this.value = value.codePoints().asLongStream().boxed().collect(Collectors.toList());
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
