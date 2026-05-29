package com.slick.parser.nodes;

import com.slick.parser.ASTNode;

import java.util.List;

public class ProgramNode extends ASTNode {
    public final List<ASTNode> declarations;

    public ProgramNode(List<ASTNode> declarations) {
        this.declarations = declarations;
    }

}
