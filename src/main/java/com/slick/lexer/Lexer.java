package com.slick.lexer;

import java.util.HashMap;
import java.util.Map;

public class Lexer {
    private final String source;
    private int pos= 0;
    private int line= 1;
    private int column= 1;
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    static {
        KEYWORDS.put("pit", TokenType.PIT);
        KEYWORDS.put("stay", TokenType.STAY);
        KEYWORDS.put("sector", TokenType.SECTOR);
        KEYWORDS.put("race", TokenType.RACE);
        KEYWORDS.put("podium", TokenType.PODIUM);
        KEYWORDS.put("radio", TokenType.RADIO);
        KEYWORDS.put("telemetry", TokenType.TELEMETRY);
        KEYWORDS.put("lap", TokenType.LAP);
        KEYWORDS.put("flag", TokenType.FLAG);
        KEYWORDS.put("green", TokenType.GREEN);
        KEYWORDS.put("yellow", TokenType.YELLOW);
    }


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

    private void skipWhiteSpace(){
        char charPeek = peek();
        while(charPeek==' '||charPeek=='\t'||charPeek=='\n') {
            advance();
            charPeek = peek();
        }
        if(charPeek=='/' && peekNext()=='/'){
            while (charPeek !='\n'&&charPeek !='\0') {
                advance();
                charPeek = peek();
            }
            this.skipWhiteSpace();
        }
    }

    private Token readNumber(char fist){
        int colunmNumber = column;
        StringBuilder number = new StringBuilder();
        number.append(fist);
        while(Character.isDigit(peek())){
            number.append(advance());
        }
        return new Token(TokenType.NUMBER,number.toString(),this.line,colunmNumber);
    }

    private Token readIdentifierOrKeyword(char fist){
        int colunmNumber = column;
        StringBuilder keyWord = new StringBuilder();
        keyWord.append(fist);
        while(Character.isLetterOrDigit(peek())){
            keyWord.append(advance());
        }
        String keyWordString = keyWord.toString();
        TokenType keyWordType = KEYWORDS.getOrDefault(keyWordString,TokenType.IDENTIFIER);
        return new Token(keyWordType,keyWordString,this.line,colunmNumber);
    }
}
