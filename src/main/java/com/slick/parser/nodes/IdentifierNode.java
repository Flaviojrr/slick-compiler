package com.slick.parser.nodes;

import com.slick.parser.ASTNode;

public class IdentifierNode extends ASTNode {
    public final String name;

    public IdentifierNode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "IdentifierNode{" +
                "name='" + name + '\'' +
                '}';
    }
}
