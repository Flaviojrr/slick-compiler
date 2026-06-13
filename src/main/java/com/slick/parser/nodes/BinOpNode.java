package com.slick.parser.nodes;

import com.slick.parser.ASTNode;

public class BinOpNode extends ASTNode {
    public final ASTNode left;
    public final String operator;
    public final ASTNode right;

    public BinOpNode(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return "BinOpNode{" +
                "left=" + left +
                ", operator='" + operator + '\'' +
                ", right=" + right +
                '}';
    }
}
