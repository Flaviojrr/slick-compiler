package com.slick;

import com.slick.lexer.Lexer;
import com.slick.lexer.Token;

import java.util.List;

public class Compiler {
    public static void main(String[] args) {
        String teste = "pit (voltas > 0) {\n" +
                "    radio voltas;\n" +
                "}";
        Lexer testeLexer = new Lexer(teste);
        List<Token> List = testeLexer.tokenize();
        for(Token lalala : List){
            System.out.println(lalala.toString());
        }
    }
}
