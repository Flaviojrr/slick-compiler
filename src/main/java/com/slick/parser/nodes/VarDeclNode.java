package com.slick.parser.nodes;

import com.slick.parser.ASTNode;

public class VarDeclNode extends ASTNode {
    public final String type;
    public final String name;
    public final ASTNode initializer;

    public VarDeclNode(String type, String name, ASTNode initializer) {
        this.type = type;
        this.name = name;
        this.initializer = initializer;
    }
}
