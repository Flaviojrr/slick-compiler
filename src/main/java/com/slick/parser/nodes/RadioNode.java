package com.slick.parser.nodes;

import com.slick.parser.ASTNode;

public class RadioNode extends ASTNode {
    public final ASTNode expression;

    public RadioNode(ASTNode expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "RadioNode{" +
                "expression=" + expression +
                '}';
    }
}
