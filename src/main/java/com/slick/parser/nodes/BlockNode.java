package com.slick.parser.nodes;

import com.slick.parser.ASTNode;

import java.util.List;

public class BlockNode extends ASTNode {
    public final List<ASTNode> statements;

    public BlockNode(List<ASTNode> statements) {
        this.statements = statements;
    }
}
