package com.slick;

import com.slick.codegen.Instruction;
import com.slick.codegen.TACGenerator;
import com.slick.lexer.Lexer;
import com.slick.lexer.Token;
import com.slick.lexer.TokenType;
import com.slick.parser.ASTPrinter;
import com.slick.parser.Parser;
import com.slick.parser.nodes.ProgramNode;
import com.slick.semantic.SemanticAnalyzer;
import com.slick.vm.SlickVM;

import java.util.List;

/**
 * Ponto de entrada do compilador SLICK.
 *
 * Orquestra todas as cinco fases de compilação em sequência:
 * A) Análise Léxica    — transforma o código fonte em tokens
 * B) Análise Sintática — transforma os tokens em AST
 * C) Análise Semântica — valida a AST verificando erros lógicos
 * D) Geração de TAC    — gera código intermediário de três endereços
 * E) Execução (VM)     — interpreta e executa o programa
 *
 * Uso: java Compiler <arquivo.slick>
 */
public class Compiler {
    public static void main(String[] args) throws Exception {
        System.out.println("=== SLICK COMPILER ===\n");

        if (args.length == 0) {
            System.out.println("Uso: java Compiler <arquivo.slick>");
            System.exit(1);
        }

        // Fase A — Análise Léxica
        // Lê o arquivo .slick e transforma o código em uma lista de tokens.
        // Se encontrar um caractere inválido, reporta o erro e encerra.
        String fonte = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(args[0])));
        Lexer lexer = new Lexer(fonte);
        List<Token> tokens = lexer.tokenize();
        for (Token token : tokens) {
            if (token.getType().equals(TokenType.ERROR)) {
                System.out.println("[LEXER]   ERROR - caractere invalido '" + token.getValue() + "' na linha " + token.getLine());
                System.exit(1);
            }
        }
        System.out.println("[LEXER]    OK - " + tokens.size() + " tokens generated");

        // Fase B — Análise Sintática
        // Constrói a Árvore de Sintaxe Abstrata (AST) a partir da lista de tokens
        // usando o método de Descida Recursiva.
        Parser parser = new Parser(tokens);
        ProgramNode ast = parser.parseProgram();
        System.out.println("[PARSER]   OK - AST built");

        // Fase C — Análise Semântica
        // Percorre a AST e verifica consistência lógica:
        // detecta variáveis não declaradas e declarações duplicadas.
        SemanticAnalyzer semantic = new SemanticAnalyzer();
        semantic.analyze(ast);
        System.out.println("[SEMANTIC] OK - no errors");

        // Fase D — Geração de Código Intermediário (TAC)
        // Percorre a AST e gera instruções de Código de Três Endereços.
        // Cada instrução tem o formato: op resultado arg1 arg2
        TACGenerator tac = new TACGenerator();
        List<Instruction> instructions = tac.generate(ast);
        System.out.println("\n[TAC] Intermediate code:");
        System.out.println("-----------------------");
        for (Instruction inst : instructions) {
            System.out.println(inst.op + "\t" + inst.result + "\t" + inst.arg1 + "\t" + inst.arg2);
        }
        System.out.println("-----------------------");

        // Fase E — Execução (SlickVM)
        // A SlickVM interpreta a AST diretamente e executa o programa,
        // gerando a saída final para o usuário.
        System.out.println("\n[VM] Running...");
        System.out.println("-----------------------");
        System.out.println("Output:");
        SlickVM vm = new SlickVM();
        vm.execute(ast);
        System.out.println("-----------------------");
    }
}
