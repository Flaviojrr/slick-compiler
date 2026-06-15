package com.slick.vm;

import com.slick.parser.ASTNode;
import com.slick.parser.nodes.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Fase E — Máquina Virtual SLICK (SlickVM).
 *
 * A SlickVM interpreta e executa a AST diretamente, sem gerar
 * código de máquina real. É uma abordagem de interpretação em árvore
 * (tree-walking interpreter).
 *
 * Possui dois métodos principais:
 * - execute(node): executa statements (ações sem valor de retorno)
 * - evaluate(node): avalia expressões (retorna um valor)
 *
 * As variáveis são armazenadas em memória (HashMap) durante a execução.
 * Funções são armazenadas em um mapa separado pelo nome.
 */
public class SlickVM {

    /**
     * Memória da VM — armazena variáveis e seus valores.
     * A chave é o nome da variável, o valor pode ser Integer ou Boolean.
     */
    Map<String, Object> memory = new HashMap<>();

    /**
     * Mapa de funções declaradas no programa.
     * A chave é o nome da função, o valor é o nó FuncDeclNode da AST.
     */
    Map<String, FuncDeclNode> functions = new HashMap<>();

    public SlickVM() {}

    /**
     * Executa um nó da AST como statement.
     * É chamado recursivamente para cada nó filho.
     *
     * @param node nó da AST a executar
     */
    public void execute(ASTNode node) {

        if (node instanceof ProgramNode) {
            // Nó raiz — executa cada declaração do programa em sequência
            ProgramNode p = (ProgramNode) node;
            for (ASTNode decl : p.declarations) execute(decl);

        } else if (node instanceof FuncDeclNode) {
            // Declaração de função — registra no mapa para chamada posterior
            FuncDeclNode f = (FuncDeclNode) node;
            functions.put(f.getName(), f);

        } else if (node instanceof BlockNode) {
            // Bloco de código — executa cada statement dentro do bloco
            BlockNode b = (BlockNode) node;
            for (ASTNode stmt : b.statements) execute(stmt);

        } else if (node instanceof VarDeclNode) {
            // Declaração de variável — inicializa na memória.
            // Se tiver inicializador, avalia e armazena.
            // Caso contrário, usa o valor padrão do tipo (0 para lap, false para flag)
            VarDeclNode v = (VarDeclNode) node;
            if (v.initializer != null) {
                memory.put(v.name, evaluate(v.initializer));
            } else if (v.type.equals("lap")) {
                memory.put(v.name, 0);
            } else {
                memory.put(v.name, false);
            }

        } else if (node instanceof AssignNode) {
            // Atribuição — avalia o valor e atualiza a variável na memória
            AssignNode a = (AssignNode) node;
            memory.put(a.name, evaluate(a.value));

        } else if (node instanceof IfNode) {
            // Condicional pit/stay — avalia a condição e executa o bloco correto
            IfNode i = (IfNode) node;
            Boolean condition = (Boolean) evaluate(i.condition);
            if (condition) execute(i.thenBlock);
            else if (i.elseBlock != null) execute(i.elseBlock);

        } else if (node instanceof WhileNode) {
            // Laço sector — avalia a condição e repete o corpo enquanto for true
            WhileNode w = (WhileNode) node;
            while ((Boolean) evaluate(w.condition)) {
                execute(w.body);
            }

        } else if (node instanceof RadioNode) {
            // Impressão — avalia a expressão e imprime no terminal
            RadioNode r = (RadioNode) node;
            System.out.println(evaluate(r.expression));

        } else if (node instanceof TelemetryNode) {
            // Leitura de entrada — exibe prompt, lê valor do usuário
            // e armazena na memória como Integer
            TelemetryNode t = (TelemetryNode) node;
            Scanner scanner = new Scanner(System.in);
            System.out.print("Digite o valor para " + t.name + ": ");
            String linha = scanner.nextLine().trim();
            memory.put(t.name, Integer.parseInt(linha));

        } else if (node instanceof ReturnNode) {
            // Retorno de função — não implementado na versão atual
        }
    }

    /**
     * Avalia um nó de expressão da AST e retorna o valor resultante.
     * É chamado recursivamente para expressões compostas.
     *
     * @param node nó de expressão a avaliar
     * @return Integer para expressões aritméticas, Boolean para comparações
     */
    public Object evaluate(ASTNode node) {

        if (node instanceof LiteralNode) {
            // Literal — retorna o valor diretamente (Integer ou Boolean)
            LiteralNode l = (LiteralNode) node;
            return l.value;

        } else if (node instanceof IdentifierNode) {
            // Identificador — busca o valor atual na memória
            IdentifierNode i = (IdentifierNode) node;
            return memory.get(i.name);

        } else if (node instanceof BinOpNode) {
            // Operação binária — avalia os dois lados e aplica o operador
            BinOpNode b = (BinOpNode) node;
            Object left = evaluate(b.left);
            Object right = evaluate(b.right);
            if (b.operator.equals("+"))  return (Integer) left + (Integer) right;
            else if (b.operator.equals("-"))  return (Integer) left - (Integer) right;
            else if (b.operator.equals("*"))  return (Integer) left * (Integer) right;
            else if (b.operator.equals("/"))  return (Integer) left / (Integer) right;
            else if (b.operator.equals("==")) return left.equals(right);
            else if (b.operator.equals("!=")) return !left.equals(right);
            else if (b.operator.equals("<"))  return (Integer) left < (Integer) right;
            else if (b.operator.equals(">"))  return (Integer) left > (Integer) right;
            return null;

        } else if (node instanceof CallNode) {
            // Chamada de função — busca a função e executa o corpo
            CallNode c = (CallNode) node;
            FuncDeclNode func = functions.get(c.name);
            execute(func.getBody());
            return null;
        }

        return null;
    }
}
