package com.slick.parser.nodes;

import com.slick.parser.ASTNode;

public class IfNode extends ASTNode {
    public final ASTNode condition;
    public final BlockNode thenBlock;
    public final BlockNode elseBlock;

    public IfNode(ASTNode condition, BlockNode thenBlock, BlockNode elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public String toString() {
        return "IfNode{" +
                "condition=" + condition +
                ", thenBlock=" + thenBlock +
                ", elseBlock=" + elseBlock +
                '}';
    }
}
