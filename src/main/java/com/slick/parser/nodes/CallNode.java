package com.slick.parser.nodes;

import com.slick.parser.ASTNode;

import java.util.List;

public class CallNode extends ASTNode {
    public final String name;
    public final List<ASTNode> arguments;

    public CallNode(String name, List<ASTNode> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "CallNode{" +
                "name='" + name + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
