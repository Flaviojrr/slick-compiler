package com.slick.parser;

import com.slick.parser.nodes.FuncDeclNode;
import com.slick.parser.nodes.IfNode;
import com.slick.parser.nodes.ProgramNode;

public class ASTPrinter {
   public void print (ASTNode node){
       System.out.println(node.toString());
   }
}
