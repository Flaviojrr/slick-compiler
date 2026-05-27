package com.slick.parser;

import com.slick.lexer.Token;
import com.slick.lexer.TokenType;

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

}
