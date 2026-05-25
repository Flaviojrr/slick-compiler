package com.slick;

import com.slick.lexer.Lexer;
import com.slick.lexer.Token;
import com.slick.lexer.TokenType;

import java.text.BreakIterator;
import java.util.List;

public class Compiler {
    public static void main(String[] args) {
        String teste = "pit (voltas @ 0) {\n" +
                "    radio voltas;\n" +
                "}";
        Lexer testeLexer = new Lexer(teste);
        List<Token> List = testeLexer.tokenize();
        for(Token token : List){
            if(token.getType().equals(TokenType.ERROR)){
                System.out.println("Erro léxico: caractere invalido '"+token.getValue()+"' na linha "+token.getLine()+",coluna "+token.getColumn());
                System.exit(1);
            }
        }
        for(Token token : List){
            System.out.println(token.toString());
        }
    }
}
