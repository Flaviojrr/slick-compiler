package com.slick.parser;

import com.slick.lexer.Token;
import com.slick.lexer.TokenType;
import com.slick.parser.nodes.BlockNode;
import com.slick.parser.nodes.FuncDeclNode;
import com.slick.parser.nodes.ProgramNode;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private int pos =0;
    private List<Token> tokens;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token peek(){
        if(pos>=tokens.size()) return tokens.get(tokens.size()-1);
        return tokens.get(pos);
    }

    private Token advance(){
        Token token = tokens.get(pos);
        if(pos<tokens.size()){
            pos++;
        }
        return token;
    }

    private boolean check(TokenType type){
        if(type.equals(peek().getType())) return true;
        return false;
    }

    private Token eat(TokenType type){
        if(check(type)) return advance();
        throw new RuntimeException("Erro Sintático: esperava " + type+" mas encontrou"+ peek().getType()+" na linha "+peek().getLine()+", coluna "+peek().getColumn());
    }

    private boolean isAtEnd(){
        return check(TokenType.EOF);
    }

    public ProgramNode parseProgram(){
        List<ASTNode> declarations = new ArrayList<>();
        if(isAtEnd()) declarations.add(parserDeclaration());
        return new ProgramNode(declarations);
    }

    private ASTNode parserDeclaration(){
        if(check(TokenType.RACE)) return parseFundDecl();
        return parseStatenebt();
    }

    private FuncDeclNode parseFundDecl() {
        List<String[]> params = new ArrayList<>();
        String lapOrFlag="";
        String param;
        eat(TokenType.RACE);
        String name = eat(TokenType.IDENTIFIER).toString();
        eat(TokenType.LPAREN);
        while (!peek().equals(TokenType.RPAREN)){
            if(check(TokenType.LAP)) {
                eat(TokenType.LAP);
                lapOrFlag = peek().toString();
            }
            if(check(TokenType.FLAG)) {
                eat(TokenType.FLAG);
                lapOrFlag = peek().toString();
            }
            eat(TokenType.IDENTIFIER);
            param=peek().toString();
            params.add(new String[]{lapOrFlag,param});
            if(check(TokenType.COMMA)) eat(TokenType.COMMA);
        }
        eat(TokenType.RPAREN);
        BlockNode body = parseBlock();
        return new FuncDeclNode(name,params,body);
    }



    private ASTNode parseStatenebt() {
        return null;
    }

    private BlockNode parseBlock() {
        return null;
    }

}
