package com.slick.parser.nodes;

import com.slick.parser.ASTNode;

public class LiteralNode extends ASTNode {
    public final Object value;
    public final String type;

    public LiteralNode(Object value, String type) {
        this.value = value;
        this.type = type;
    }
}
