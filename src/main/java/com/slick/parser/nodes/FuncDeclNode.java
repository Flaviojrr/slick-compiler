package com.slick.parser.nodes;

import com.slick.parser.ASTNode;

import java.util.List;

public class FuncDeclNode extends ASTNode {
    private String name;
    private List<String[]> params;
    private BlockNode body;

    public FuncDeclNode(String name, List<String[]> params, BlockNode body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String[]> getParams() {
        return params;
    }

    public void setParams(List<String[]> params) {
        this.params = params;
    }

    public BlockNode getBody() {
        return body;
    }

    public void setBody(BlockNode body) {
        this.body = body;
    }
}
