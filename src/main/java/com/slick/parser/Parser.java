package com.slick.parser;

import com.slick.lexer.Token;
import com.slick.lexer.TokenType;
import com.slick.parser.nodes.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Fase B — Análise Sintática (Parser).
 *
 * O Parser consome a lista de tokens gerada pelo Lexer e constrói
 * a Árvore de Sintaxe Abstrata (AST) usando o método de Descida Recursiva.
 *
 * Cada regra da gramática BNF da linguagem SLICK corresponde a um
 * método dedicado nesta classe. Os métodos se chamam recursivamente
 * seguindo a estrutura hierárquica da gramática.
 *
 * Entrada: List<Token> produzida pelo Lexer
 * Saída:   ProgramNode — raiz da AST
 */
public class Parser {

    /** Posição atual na lista de tokens */
    private int pos = 0;

    /** Lista de tokens a ser consumida */
    private List<Token> tokens;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Retorna o token atual sem avançar o cursor.
     * Se pos estiver além do fim da lista, retorna o último token (EOF).
     */
    private Token peek() {
        if (pos >= tokens.size()) return tokens.get(tokens.size() - 1);
        return tokens.get(pos);
    }

    /**
     * Consome e retorna o token atual, avançando o cursor para o próximo.
     */
    private Token advance() {
        Token token = tokens.get(pos);
        if (pos < tokens.size()) {
            pos++;
        }
        return token;
    }

    /**
     * Verifica se o token atual é do tipo esperado sem consumi-lo.
     *
     * @param type tipo a verificar
     * @return true se o token atual for do tipo informado
     */
    private boolean check(TokenType type) {
        if (type.equals(peek().getType())) return true;
        return false;
    }

    /**
     * Consome o token atual se for do tipo esperado.
     * Lança RuntimeException com mensagem detalhada se o tipo não bater.
     *
     * @param type tipo esperado
     * @return o token consumido
     */
    private Token eat(TokenType type) {
        if (check(type)) return advance();
        throw new RuntimeException("Erro Sintático: esperava " + type + " mas encontrou " + peek().getType() + " na linha " + peek().getLine() + ", coluna " + peek().getColumn());
    }

    /**
     * Verifica se chegamos ao fim do arquivo (token EOF).
     */
    private boolean isAtEnd() {
        return check(TokenType.EOF);
    }

    /**
     * Ponto de entrada do Parser.
     * Constrói o nó raiz lendo todas as declarações até o EOF.
     *
     * @return ProgramNode com todas as declarações do programa
     */
    public ProgramNode parseProgram() {
        List<ASTNode> declarations = new ArrayList<>();
        while (!isAtEnd()) declarations.add(parseDeclaration());
        return new ProgramNode(declarations);
    }

    /**
     * Decide se a próxima construção é uma declaração de função (race)
     * ou um statement comum.
     */
    private ASTNode parseDeclaration() {
        if (check(TokenType.RACE)) return parseFuncDecl();
        return parseStatement();
    }

    /**
     * Lê uma declaração de função no formato:
     * race nome(tipo param, ...) { corpo }
     */
    private FuncDeclNode parseFuncDecl() {
        List<String[]> params = new ArrayList<>();
        String lapOrFlag = "";
        String param;
        eat(TokenType.RACE);
        String name = eat(TokenType.IDENTIFIER).getValue();
        eat(TokenType.LPAREN);
        while (!check(TokenType.RPAREN)) {
            if (check(TokenType.LAP)) {
                lapOrFlag = eat(TokenType.LAP).getValue();
            }
            if (check(TokenType.FLAG)) {
                lapOrFlag = eat(TokenType.FLAG).getValue();
            }
            param = eat(TokenType.IDENTIFIER).getValue();
            params.add(new String[]{lapOrFlag, param});
            if (check(TokenType.COMMA)) eat(TokenType.COMMA);
        }
        eat(TokenType.RPAREN);
        BlockNode body = parseBlock();
        return new FuncDeclNode(name, params, body);
    }

    /**
     * Dispatcher de statements — identifica o tipo pelo token atual
     * e delega para o método de parsing correspondente.
     */
    private ASTNode parseStatement() {
        if (check(TokenType.PIT))                          return parseIf();
        else if (check(TokenType.SECTOR))                  return parserWhile();
        else if (check(TokenType.PODIUM))                  return parseReturn();
        else if (check(TokenType.RADIO))                   return parseRadio();
        else if (check(TokenType.TELEMETRY))               return parseTelemetry();
        else if (check(TokenType.LAP) || check(TokenType.FLAG)) return parseVarDecl();
        else if (check(TokenType.IDENTIFIER))              return parseAssign();
        else throw new RuntimeException("Erro Sintático: token inesperado '" + peek().getValue() + " na linha " + peek().getLine() + ", coluna " + peek().getColumn());
    }

    /**
     * Lê uma atribuição no formato: nome = expressao;
     */
    private AssignNode parseAssign() {
        String name = eat(TokenType.IDENTIFIER).getValue();
        eat(TokenType.ASSIGN);
        ASTNode value = parseExpression();
        eat(TokenType.SEMICOLON);
        return new AssignNode(name, value);
    }

    /**
     * Lê uma declaração de variável no formato: lap/flag nome = expressao;
     * O inicializador é opcional.
     */
    private VarDeclNode parseVarDecl() {
        String type;
        ASTNode initializer;
        if (check(TokenType.LAP)) type = eat(TokenType.LAP).getValue();
        else type = eat(TokenType.FLAG).getValue();
        String name = eat(TokenType.IDENTIFIER).getValue();
        if (check(TokenType.ASSIGN)) {
            eat(TokenType.ASSIGN);
            initializer = parseExpression();
        } else {
            initializer = null;
        }
        eat(TokenType.SEMICOLON);
        return new VarDeclNode(type, name, initializer);
    }

    /**
     * Lê um comando de leitura no formato: telemetry nome;
     */
    private TelemetryNode parseTelemetry() {
        eat(TokenType.TELEMETRY);
        String nome = eat(TokenType.IDENTIFIER).getValue();
        eat(TokenType.SEMICOLON);
        return new TelemetryNode(nome);
    }

    /**
     * Lê um comando de impressão no formato: radio expressao;
     */
    private RadioNode parseRadio() {
        eat(TokenType.RADIO);
        ASTNode expression = parseExpression();
        eat(TokenType.SEMICOLON);
        return new RadioNode(expression);
    }

    /**
     * Lê um retorno de função no formato: podium expressao?;
     * A expressão é opcional — podium; é válido.
     */
    private ReturnNode parseReturn() {
        ASTNode value = null;
        eat(TokenType.PODIUM);
        if (check(TokenType.SEMICOLON)) {
            eat(TokenType.SEMICOLON);
            return new ReturnNode(value);
        }
        value = parseExpression();
        eat(TokenType.SEMICOLON);
        return new ReturnNode(value);
    }

    /**
     * Lê um laço no formato: sector (expressao) { corpo }
     */
    private WhileNode parserWhile() {
        eat(TokenType.SECTOR);
        eat(TokenType.LPAREN);
        ASTNode condition = parseExpression();
        eat(TokenType.RPAREN);
        BlockNode body = parseBlock();
        return new WhileNode(condition, body);
    }

    /**
     * Lê um condicional no formato: pit (expressao) { } stay { }
     * O bloco stay é opcional.
     */
    private IfNode parseIf() {
        eat(TokenType.PIT);
        eat(TokenType.LPAREN);
        ASTNode condition = parseExpression();
        BlockNode elseBlock = null;
        eat(TokenType.RPAREN);
        BlockNode thenBlock = parseBlock();
        if (check(TokenType.STAY)) {
            eat(TokenType.STAY);
            elseBlock = parseBlock();
        }
        return new IfNode(condition, thenBlock, elseBlock);
    }

    /**
     * Lê um bloco de código no formato: { statements }
     */
    private BlockNode parseBlock() {
        eat(TokenType.LBRACE);
        List<ASTNode> statements = new ArrayList<>();
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(parseStatement());
        }
        eat(TokenType.RBRACE);
        return new BlockNode(statements);
    }

    /**
     * Porta de entrada para parsing de expressões.
     * Delega para parseEquality() — nível mais baixo de precedência.
     */
    private ASTNode parseExpression() {
        return parseEquality();
    }

    /**
     * Trata operadores de igualdade: == e !=
     * Menor precedência entre os operadores binários.
     */
    private ASTNode parseEquality() {
        ASTNode left = parseComparison();
        while (check(TokenType.EQ) || check(TokenType.NEQ)) {
            String valor = peek().getValue();
            advance();
            ASTNode right = parseComparison();
            left = new BinOpNode(left, valor, right);
        }
        return left;
    }

    /**
     * Trata operadores de comparação: < e >
     */
    private ASTNode parseComparison() {
        ASTNode left = parseTerm();
        while (check(TokenType.LT) || check(TokenType.GT)) {
            String valor = peek().getValue();
            advance();
            ASTNode right = parseTerm();
            left = new BinOpNode(left, valor, right);
        }
        return left;
    }

    /**
     * Trata operadores de soma e subtração: + e -
     */
    private ASTNode parseTerm() {
        ASTNode left = parseFactor();
        while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
            String valor = peek().getValue();
            advance();
            ASTNode right = parseFactor();
            left = new BinOpNode(left, valor, right);
        }
        return left;
    }

    /**
     * Trata operadores de multiplicação e divisão: * e /
     * Maior precedência entre os operadores binários.
     */
    private ASTNode parseFactor() {
        ASTNode left = parserUnary();
        while (check(TokenType.STAR) || check(TokenType.SLASH)) {
            String valor = peek().getValue();
            advance();
            ASTNode right = parserUnary();
            left = new BinOpNode(left, valor, right);
        }
        return left;
    }

    /**
     * Trata o operador unário de negação: -
     * Chamado recursivamente para permitir --x.
     */
    private ASTNode parserUnary() {
        if (check(TokenType.MINUS)) {
            advance();
            ASTNode resultado = parserUnary();
            return new BinOpNode(new LiteralNode(0, "lap"), "-", resultado);
        }
        return parserPrimary();
    }

    /**
     * Lê o elemento mais básico de uma expressão:
     * - Número literal
     * - Booleano literal (green/yellow)
     * - Identificador ou chamada de função
     * - Expressão entre parênteses
     */
    private ASTNode parserPrimary() {
        if (check(TokenType.NUMBER)) {
            String valorString = advance().getValue();
            int valorInt = Integer.parseInt(valorString);
            return new LiteralNode(valorInt, "lap");
        } else if (check(TokenType.GREEN)) {
            advance();
            return new LiteralNode(true, "flag");
        } else if (check(TokenType.YELLOW)) {
            advance();
            return new LiteralNode(false, "flag");
        } else if (check(TokenType.IDENTIFIER)) {
            String nome = advance().getValue();
            if (check(TokenType.LPAREN)) {
                // Chamada de função — lê a lista de argumentos
                eat(TokenType.LPAREN);
                List<ASTNode> arguments = new ArrayList<>();
                while (!check(TokenType.RPAREN)) {
                    arguments.add(parseExpression());
                    if (check(TokenType.COMMA)) eat(TokenType.COMMA);
                }
                eat(TokenType.RPAREN);
                return new CallNode(nome, arguments);
            }
            return new IdentifierNode(nome);
        } else if (check(TokenType.LPAREN)) {
            // Expressão agrupada entre parênteses
            eat(TokenType.LPAREN);
            ASTNode expr = parseExpression();
            eat(TokenType.RPAREN);
            return expr;
        } else {
            throw new RuntimeException("Erro Sintático: token inesperado '" + peek().getValue() + " na linha " + peek().getLine() + ", coluna " + peek().getColumn());
        }
    }
}
