package com.slick.lexer;

public class Lexer {
    private final String source;
    private int pos= 0;
    private int line= 1;
    private int column= 1;

    public Lexer(String source) {
        this.source = source;
    }

    private char peek(){
        if(pos>=source.length())
            return '\0';
        return source.charAt(pos);
    }

    private char peekNext(){
        if (pos+1>=source.length())
            return '\0';
        return source.charAt(pos+1);
    }

    private char advance(){
        char posChar = source.charAt(pos);
        pos += 1;
        column+= 1;
        if(posChar=='\n')
            line += 1;
            column=0;
        return posChar;
    }
}
