package me.yzyzsun.jiro.nodes;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import me.yzyzsun.jiro.runtime.*;

@TypeSystemReference(JiroTypes.class)
public abstract class ExpressionNode extends Node {
    public abstract Object executeGeneric(VirtualFrame frame);

    public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
        return JiroTypesGen.expectLong(executeGeneric(frame));
    }

    public JiroBigInteger executeBigInteger(VirtualFrame frame) throws UnexpectedResultException {
        return JiroTypesGen.expectJiroBigInteger(executeGeneric(frame));
    }

    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        return JiroTypesGen.expectDouble(executeGeneric(frame));
    }

    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        return JiroTypesGen.expectBoolean(executeGeneric(frame));
    }

    public String executeString(VirtualFrame frame) throws UnexpectedResultException {
        return JiroTypesGen.expectString(executeGeneric(frame));
    }

    public JiroTuple executeTuple(VirtualFrame frame) throws UnexpectedResultException {
        return JiroTypesGen.expectJiroTuple(executeGeneric(frame));
    }

    public JiroList executeList(VirtualFrame frame) throws UnexpectedResultException {
        return JiroTypesGen.expectJiroList(executeGeneric(frame));
    }

    public JiroCons executeCons(VirtualFrame frame) throws UnexpectedResultException {
        return JiroTypesGen.expectJiroCons(executeGeneric(frame));
    }

    public JiroBinary executeBinary(VirtualFrame frame) throws UnexpectedResultException {
        return JiroTypesGen.expectJiroBinary(executeGeneric(frame));
    }

    public JiroNil executeNil(VirtualFrame frame) throws UnexpectedResultException {
        return JiroTypesGen.expectJiroNil(executeGeneric(frame));
    }
}
