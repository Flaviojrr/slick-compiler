package com.slick.parser.nodes;

import com.slick.parser.ASTNode;

public class WhileNode extends ASTNode {
    public final ASTNode condition;
    public final BlockNode body;

    public WhileNode(ASTNode condition, BlockNode body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return "WhileNode{" +
                "condition=" + condition +
                ", body=" + body +
                '}';
    }
}
