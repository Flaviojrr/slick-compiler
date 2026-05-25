package com.slick.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.slick.lexer.TokenType.*;
/**
 * Fase A — Análise Léxica (Scanner)
 *
 * O Lexer é responsável por transformar o código fonte (.slick) em uma
 * lista de tokens. Ele lê o texto caractere por caractere, agrupa os
 * caracteres em unidades com significado (tokens) e descarta o que não
 * importa para o compilador (espaços, tabs, quebras de linha e comentários).
 *
 * Entrada: String com o código fonte completo
 * Saída:List<Token> com todos os tokens reconhecidos
 */
public class Lexer {
    private final String source;
    private int pos= 0;
    private int line= 1;
    private int column= 1;
    /**
     * Mapa estático de palavras reservadas da linguagem SLICK.
     * Associa cada keyword ao seu TokenType correspondente.
     * Usado pelo readIdentifierOrKeyword() para distinguir
     * keywords de identificadores criados pelo usuário.
     */
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


    /**
     * Construtor do Lexer.
     * Recebe o código fonte completo como String e inicializa
     * os contadores de posição, linha e coluna.
     */
    public Lexer(String source) {
        this.source = source;
    }

    /**
     * Espia o caractere atual sem consumi-lo.
     * Não avança o pos — apenas olha o que está na posição atual.
     * Retorna '\0' se o fim do arquivo foi atingido, sinalizando
     * que não há mais caracteres para ler.
     */
    private char peek(){
        if(pos>=source.length())
            return '\0';
        return source.charAt(pos);
    }

    /**
     * Espia o próximo caractere (pos + 1) sem consumi-lo.
     * Usado para reconhecer operadores de dois caracteres como
     * == e != — é preciso ver o segundo char antes de decidir
     * qual token emitir.
     * Retorna '\0' se não houver próximo caractere.
     */
    private char peekNext(){
        if (pos+1>=source.length())
            return '\0';
        return source.charAt(pos+1);
    }

    /**
     * Consome o caractere atual e avança a posição de leitura.
     * Atualiza o contador de coluna a cada caractere consumido.
     * Quando encontra uma quebra de linha '\n', incrementa o
     * contador de linha e reseta a coluna para 1 — início da
     * nova linha.
     * Retorna o caractere que foi consumido.
     */
    private char advance(){
        char posChar = source.charAt(pos);
        pos++;
        column++;
        if(posChar=='\n') {
            line++;
            column = 1;
        }
        return posChar;
    }

    /**
     * Descarta caracteres que não formam tokens:
     * espaços, tabs, quebras de linha e comentários de linha (//).
     * É chamado no início de cada nextToken() para garantir que
     * o próximo caractere a ser processado seja sempre útil.
     * Após descartar um comentário, chama a si mesmo recursivamente
     * porque pode haver mais espaços ou comentários em seguida.
     */
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

    /**
     * Lê um literal numérico inteiro a partir do primeiro dígito já consumido.
     * Acumula todos os dígitos seguintes e retorna um Token do tipo NUMBER
     * com o valor completo e a posição onde o número começou no código.
     *
     * @param first o primeiro dígito já consumido pelo nextToken()
     * @return Token do tipo NUMBER com o valor acumulado
     */
    private Token readNumber(char fist){
        int colunmNumber = column-1;
        StringBuilder number = new StringBuilder();
        number.append(fist);
        while(Character.isDigit(peek())){
            number.append(advance());
        }
        return new Token(TokenType.NUMBER,number.toString(),this.line,colunmNumber);
    }

    /**
     * Lê um identificador ou palavra reservada a partir da primeira letra já consumida.
     * Acumula letras, dígitos e underscores. Ao final, consulta o mapa de keywords
     * para decidir se a palavra é uma keyword da SLICK (pit, race, lap...)
     * ou um identificador criado pelo usuário (voltas, resultado, i...).
     *
     * @param first a primeira letra já consumida pelo nextToken()
     * @return Token com o tipo correto (keyword ou IDENTIFIER) e o valor acumulado
     */
    private Token readIdentifierOrKeyword(char fist){
        int colunmNumber = column-1;
        StringBuilder keyWord = new StringBuilder();
        keyWord.append(fist);
        while(Character.isLetterOrDigit(peek())){
            keyWord.append(advance());
        }
        String keyWordString = keyWord.toString();
        TokenType keyWordType = KEYWORDS.getOrDefault(keyWordString,TokenType.IDENTIFIER);
        return new Token(keyWordType,keyWordString,this.line,colunmNumber);
    }

    /**
     * Produz e retorna o próximo token do código fonte.
     * É o coração do Lexer — decide qual token emitir com base
     * no caractere atual, delegando para readNumber() e
     * readIdentifierOrKeyword() quando necessário.
     * Para operadores de dois caracteres (== e !=), usa peekNext()
     * para espiar o próximo char antes de decidir.
     *
     * @return o próximo Token reconhecido, ou Token(ERROR) para char inválido
     */
    private Token nextToken(){
        this.skipWhiteSpace();
        if(pos>= source.length()) return new Token(EOF,"",line,column-1);
        int colunmNumber = column;
        char c = this.advance();
        switch (c){
            case '(':return new Token(TokenType.LPAREN ,String.valueOf(c),line,colunmNumber);
            case ')':return new Token(TokenType.RPAREN ,String.valueOf(c),line,colunmNumber);
            case '{':return new Token(TokenType.LBRANCE ,String.valueOf(c),line,colunmNumber);
            case '}':return new Token(TokenType.RBRANCE ,String.valueOf(c),line,colunmNumber);
            case ';':return new Token(TokenType.SEMICOLON ,String.valueOf(c),line,colunmNumber);
            case ',':return new Token(TokenType.COMMA ,String.valueOf(c),line,colunmNumber);
            case '+':return new Token(TokenType.PLUS ,String.valueOf(c),line,colunmNumber);
            case '-':return new Token(TokenType.MINUS ,String.valueOf(c),line,colunmNumber);
            case '*':return new Token(TokenType.STAR ,String.valueOf(c),line,colunmNumber);
            case '<':return new Token(TokenType.LT ,String.valueOf(c),line,colunmNumber);
            case '>':return new Token(TokenType.GT ,String.valueOf(c),line,colunmNumber);
            case '/':return new Token(TokenType.SLASH ,String.valueOf(c),line,colunmNumber);
            case '=':
                if(peek()==('=')){
                    advance();
                    return new Token(EQ,"==",line,colunmNumber);
                }
                return new Token(ASSIGN,String.valueOf(c),line,colunmNumber);
            case '!':
                if(peek()==('=')){
                    advance();
                    return new Token(NEQ,"!=",line,colunmNumber);
                }
                return new Token(ERROR,String.valueOf(c),line,colunmNumber);
            default:
                if (Character.isDigit(c)){
                    return readNumber(c);
                } else if (Character.isLetter(c)) {
                    return readIdentifierOrKeyword(c);
                }else {
                    return new Token(ERROR,String.valueOf(c),line,colunmNumber);
                }
        }
    }

    /**
     * Ponto de entrada do Lexer — único método público.
     * Chama nextToken() em loop até encontrar o token EOF,
     * acumulando todos os tokens numa lista.
     * O token EOF é incluído na lista para que o Parser saiba
     * quando o arquivo terminou.
     *
     * Entrada: nenhuma (usa o source passado no construtor)
     * Saída:   List<Token> com todos os tokens do arquivo .slick
     */
    public List<Token> tokenize(){
        List<Token> tokensList = new ArrayList<>();
        Token token;
        do {
            token = nextToken();
            tokensList.add(token);
        }while (token.getType() != TokenType.EOF);
        return tokensList;
    }
}
