package com.slick.parser.nodes;

import com.slick.parser.ASTNode;

public class TelemetryNode extends ASTNode {
    public final String name;

    public TelemetryNode(String name) {
        this.name = name;
    }
}
