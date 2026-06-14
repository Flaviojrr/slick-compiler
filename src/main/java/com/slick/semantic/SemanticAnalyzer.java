package com.slick.semantic;

import com.slick.parser.ASTNode;
import com.slick.parser.nodes.*;

public class SemanticAnalyzer {
    SymbolTable table = new SymbolTable();

    public void analyze(ASTNode node) throws Exception {
        if (node instanceof ProgramNode) {
            ProgramNode p = (ProgramNode) node;
            for (ASTNode decl : p.declarations) analyze(decl);
        } else if (node instanceof FuncDeclNode) {
            FuncDeclNode f = (FuncDeclNode) node;
            table.define(new Symbol(f.getName(), "race"));
            analyze(f.getBody());
        } else if (node instanceof BlockNode) {
            BlockNode b = (BlockNode) node;
            for (ASTNode stmt : b.statements) analyze(stmt);
        } else if (node instanceof VarDeclNode) {
            VarDeclNode v = (VarDeclNode) node;
            if (table.contains(v.name)){
                throw new Exception("Variável '" + v.name + "' já declarada.");
            }
            table.define(new Symbol(v.name, v.type));
            if (v.initializer != null) analyze(v.initializer);
        } else if (node instanceof AssignNode) {
            AssignNode a = (AssignNode) node;
            if (!table.contains(a.name)) {
                throw new Exception("Variável '" + a.name + "' não declarada.");
            }
            analyze(a.value);
        } else if (node instanceof IdentifierNode) {
            IdentifierNode i = (IdentifierNode) node;
            if (!table.contains(i.name)){
                throw new Exception("Identificador '" + i.name + "' não declarado.");
            }
        }else if (node instanceof IfNode) {
            IfNode i = (IfNode) node;
            analyze(i.condition);
            analyze(i.thenBlock);
            if (i.elseBlock != null) analyze(i.elseBlock);
        } else if (node instanceof WhileNode) {
            WhileNode w = (WhileNode) node;
            analyze(w.condition);
            analyze(w.body);
        } else if (node instanceof BinOpNode) {
            BinOpNode b = (BinOpNode) node;
            analyze(b.left);
            analyze(b.right);
        } else if (node instanceof RadioNode) {
            RadioNode r = (RadioNode) node;
            analyze(r.expression);
        } else if (node instanceof ReturnNode) {
            ReturnNode r = (ReturnNode) node;
            if (r.value != null) analyze(r.value);
        } else if (node instanceof LiteralNode) {
        } else if (node instanceof CallNode) {
        } else if (node instanceof TelemetryNode) {
        }
    }
}

