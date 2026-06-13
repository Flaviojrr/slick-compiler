package com.slick.parser.nodes;

import com.slick.parser.ASTNode;

public class ReturnNode extends ASTNode {
    public final ASTNode value;

    public ReturnNode(ASTNode value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ReturnNode{" +
                "value=" + value +
                '}';
    }
}
