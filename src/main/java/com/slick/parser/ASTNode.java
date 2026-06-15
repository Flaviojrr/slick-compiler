package com.slick.parser;

/**
 * Classe base abstrata para todos os nós da Árvore de Sintaxe Abstrata (AST).
 *
 * A AST é a estrutura central do compilador — representa o programa
 * de forma hierárquica após a análise sintática. Cada construção da
 * linguagem SLICK (declaração, statement, expressão) se torna um
 * nó concreto que estende esta classe.
 *
 * Por ser abstrata, não pode ser instanciada diretamente.
 * Todos os nós concretos (IfNode, WhileNode, BinOpNode, etc.)
 * estendem ASTNode e adicionam seus próprios campos.
 */
public abstract class ASTNode {
}
