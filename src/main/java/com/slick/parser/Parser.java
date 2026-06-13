package com.slick.parser;

import com.slick.lexer.Token;
import com.slick.lexer.TokenType;
import com.slick.parser.nodes.*;

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
        while(!isAtEnd()) declarations.add(parseDeclaration());
        return new ProgramNode(declarations);
    }

    private ASTNode parseDeclaration(){
        if(check(TokenType.RACE)) return parseFuncDecl();
        return parseStatement();
    }

    private FuncDeclNode parseFuncDecl() {
        List<String[]> params = new ArrayList<>();
        String lapOrFlag="";
        String param;
        eat(TokenType.RACE);
        String name = eat(TokenType.IDENTIFIER).getValue();
        eat(TokenType.LPAREN);
        while (!check(TokenType.RPAREN)){
            if(check(TokenType.LAP)) {
                lapOrFlag = eat(TokenType.LAP).getValue();
            }
            if(check(TokenType.FLAG)) {
                lapOrFlag = eat(TokenType.FLAG).getValue();
            }
            param = eat(TokenType.IDENTIFIER).getValue();
            params.add(new String[]{lapOrFlag,param});
            if(check(TokenType.COMMA)) eat(TokenType.COMMA);
        }
        eat(TokenType.RPAREN);
        BlockNode body = parseBlock();
        return new FuncDeclNode(name,params,body);
    }


    private ASTNode parseStatement() {
        if(check(TokenType.PIT)) return parseIf();
        else if (check(TokenType.SECTOR)) return parserWhile();
        else if (check(TokenType.PODIUM)) return parseReturn();
        else if (check(TokenType.RADIO)) return parseRadio();
        else if (check(TokenType.TELEMETRY)) return parseTelemetry();
        else if (check(TokenType.LAP)||check(TokenType.FLAG)) return parseVarDecl();
        else if (check(TokenType.IDENTIFIER)) return parseAssign();
        else throw new RuntimeException("Erro Sintático: token inesperado '" + peek().getValue()+" na linha "+peek().getLine()+", coluna "+peek().getColumn());
    }

    private AssignNode parseAssign() {
        String name;
        ASTNode value;
        name = eat(TokenType.IDENTIFIER).getValue();
        eat(TokenType.ASSIGN);
        value = parseExpression();
        eat(TokenType.SEMICOLON);
        return new AssignNode(name,value);
    }

    private VarDeclNode parseVarDecl() {
        String type;
        String name;
        ASTNode initializer;
        if(check(TokenType.LAP)) type=eat(TokenType.LAP).getValue();
        else type=eat(TokenType.FLAG).getValue();
        name = eat(TokenType.IDENTIFIER).getValue();
        if(check(TokenType.ASSIGN)){
            eat(TokenType.ASSIGN);
            initializer=parseExpression();
        }else {initializer=null;}
        eat(TokenType.SEMICOLON);
        return new VarDeclNode(type, name, initializer);
    }

    private TelemetryNode parseTelemetry() {
        eat(TokenType.TELEMETRY);
        String nome = eat(TokenType.IDENTIFIER).getValue();
        eat(TokenType.SEMICOLON);
        return new TelemetryNode(nome);
    }

    private RadioNode parseRadio() {
        eat(TokenType.RADIO);
        ASTNode expression = parseExpression();
        eat(TokenType.SEMICOLON);
        return new RadioNode(expression);
    }

    private ReturnNode parseReturn() {
        ASTNode value=null;
        eat(TokenType.PODIUM);
        if(check(TokenType.SEMICOLON)) {
            eat(TokenType.SEMICOLON);
            return new ReturnNode(value);
        }
        value = parseExpression();
        eat(TokenType.SEMICOLON);
        return new ReturnNode(value);
    }

    private WhileNode parserWhile() {
        eat(TokenType.SECTOR);
        eat(TokenType.LPAREN);
        ASTNode condition = parseExpression();
        eat(TokenType.RPAREN);
        BlockNode body = parseBlock();
        return new WhileNode(condition,body);
    }

    private IfNode parseIf() {
        eat(TokenType.PIT);
        eat(TokenType.LPAREN);
        ASTNode condition = parseExpression();
        BlockNode elseBlock = null;
        eat(TokenType.RPAREN);
        BlockNode thenBlock = parseBlock();
        if(check(TokenType.STAY)){
            eat(TokenType.STAY);
             elseBlock = parseBlock();
        }
        return new IfNode(condition,thenBlock,elseBlock);
    }

    private BlockNode parseBlock() {
        eat(TokenType.LBRACE);
        List<ASTNode> statements = new ArrayList<>();
        while (!check(TokenType.RBRACE)&& !isAtEnd()){
            statements.add(parseStatement());
        }
        eat(TokenType.RBRACE);
        return new BlockNode(statements);
    }
    private ASTNode parseExpression(){
        return parseEquality();
    }
    private ASTNode parseEquality(){
        ASTNode left = parseComparison();
        while (check(TokenType.EQ)||check(TokenType.NEQ)){
            String valor = peek().getValue();
            advance();
            ASTNode right = parseComparison();
            left = new BinOpNode(left,valor,right);
        }
        return left;
    }
    private ASTNode parseComparison(){
        ASTNode left = parseTerm();
        while (check(TokenType.LT)||check(TokenType.GT)){
            String valor = peek().getValue();
            advance();
            ASTNode right = parseTerm();
            left = new BinOpNode(left,valor,right);
        }
        return left;
    }
    private ASTNode parserUnary(){
        if(check(TokenType.MINUS)){
            advance();
            ASTNode resultado = parserUnary();
            return new BinOpNode(new LiteralNode(0,"lap"),"-",resultado);
        }
        return parserPrimary();
    }
    private ASTNode parseFactor(){
        ASTNode left = parserUnary();
        while (check(TokenType.STAR)||check(TokenType.SLASH)){
            String valor = peek().getValue();
            advance();
            ASTNode right = parserUnary();
            left = new BinOpNode(left,valor,right);
        }
        return left;
    }
    private ASTNode parseTerm(){
        ASTNode left = parseFactor();
        while (check(TokenType.PLUS)||check(TokenType.MINUS)){
            String valor = peek().getValue();
            advance();
            ASTNode right = parseFactor();
            left = new BinOpNode(left,valor,right);
        }
        return left;
    }
    private ASTNode parserPrimary(){
        if (check(TokenType.NUMBER)){
            String valorString = advance().getValue();
            int valorInt = Integer.parseInt(valorString);
            return new LiteralNode(valorInt,"lap");
        } else if (check(TokenType.GREEN)){
            advance();
            return new LiteralNode(true,"flag");
        } else if (check(TokenType.YELLOW)){
            advance();
            return new LiteralNode(false,"flag");
        }else if (check(TokenType.IDENTIFIER)){
            String nome = advance().getValue();
            if(check(TokenType.LPAREN)){
                eat(TokenType.LPAREN);
                List<ASTNode> arguments = new ArrayList<>();
                while (!check(TokenType.RPAREN)){
                    arguments.add(parseExpression());
                    if(check(TokenType.COMMA)) eat(TokenType.COMMA);
                }
                eat(TokenType.RPAREN);
                return  new CallNode(nome,arguments);
            }
            return new IdentifierNode(nome);
        }else if (check(TokenType.LPAREN)){
            eat(TokenType.LPAREN);
            ASTNode expr = parseExpression();
            eat(TokenType.RPAREN);
            return expr;
        }else throw new RuntimeException("Erro Sintático: token inesperado '" + peek().getValue()+" na linha "+peek().getLine()+", coluna "+peek().getColumn());
    }
}
