package com.slick.codegen;

import com.slick.parser.ASTNode;
import com.slick.parser.nodes.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Fase D — Geração de Código Intermediário (TAC).
 *
 * O TACGenerator percorre a AST recursivamente e produz uma lista de
 * instruções de Código de Três Endereços (Three-Address Code).
 *
 * O TAC é uma representação intermediária independente de máquina que
 * facilita a geração de código final e a aplicação de otimizações.
 *
 * Cada instrução tem o formato: op resultado arg1 arg2
 *
 * Exemplo para "resultado = x + y":
 *   ADD    t0        x    y
 *   ASSIGN resultado t0   null
 */
public class TACGenerator {

    /** Lista acumulada de instruções TAC geradas */
    private final List<Instruction> instructions = new ArrayList<>();

    /** Contador para geração de variáveis temporárias (t0, t1, t2...) */
    private int tempCount = 0;

    /** Contador para geração de labels de salto (L0, L1, L2...) */
    private int labelCount = 0;

    /**
     * Gera um novo nome de variável temporária única.
     * Cada chamada retorna um nome diferente: t0, t1, t2...
     */
    private String newTemp() {
        return "t" + tempCount++;
    }

    /**
     * Gera um novo nome de label único.
     * Cada chamada retorna um label diferente: L0, L1, L2...
     */
    private String newLabel() {
        return "L" + labelCount++;
    }

    /**
     * Ponto de entrada da geração de código.
     * Percorre o nó recebido e gera as instruções TAC correspondentes.
     * É chamado recursivamente para cada nó da AST.
     *
     * @param node nó da AST a processar
     * @return lista completa de instruções geradas até o momento
     */
    public List<Instruction> generate(ASTNode node) {

        if (node instanceof ProgramNode) {
            // Nó raiz — processa cada declaração do programa
            ProgramNode program = (ProgramNode) node;
            for (ASTNode declaration : program.declarations) {
                generate(declaration);
            }
        }

        else if (node instanceof FuncDeclNode) {
            // Declaração de função — gera um label com o nome da função
            // e processa o corpo
            FuncDeclNode f = (FuncDeclNode) node;
            instructions.add(new Instruction("LABEL", f.getName(), null, null));
            generate(f.getBody());
        }

        else if (node instanceof BlockNode) {
            // Bloco de código — processa cada statement dentro do bloco
            BlockNode block = (BlockNode) node;
            for (ASTNode statement : block.statements) {
                generate(statement);
            }
        }

        else if (node instanceof VarDeclNode) {
            // Declaração de variável com inicializador
            // Avalia a expressão inicial e gera ASSIGN
            VarDeclNode v = (VarDeclNode) node;
            if (v.initializer != null) {
                String result = generateExpr(v.initializer);
                instructions.add(new Instruction("ASSIGN", v.name, result, null));
            }
        }

        else if (node instanceof AssignNode) {
            // Atribuição — avalia o valor e gera ASSIGN
            AssignNode a = (AssignNode) node;
            String result = generateExpr(a.value);
            instructions.add(new Instruction("ASSIGN", a.name, result, null));
        }

        else if (node instanceof RadioNode) {
            // Impressão — avalia a expressão e gera PRINT
            RadioNode r = (RadioNode) node;
            String result = generateExpr(r.expression);
            instructions.add(new Instruction("PRINT", null, result, null));
        }

        else if (node instanceof TelemetryNode) {
            // Leitura de entrada — gera READ com o nome da variável destino
            TelemetryNode t = (TelemetryNode) node;
            instructions.add(new Instruction("READ", t.name, null, null));
        }

        else if (node instanceof ReturnNode) {
            // Retorno de função — avalia o valor e gera RETURN
            ReturnNode r = (ReturnNode) node;
            if (r.value != null) {
                String result = generateExpr(r.value);
                instructions.add(new Instruction("RETURN", null, result, null));
            }
        }

        else if (node instanceof IfNode) {
            // Condicional pit/stay:
            // Avalia condição → IF_FALSE para labelElse
            // Executa thenBlock → GOTO labelEnd
            // LABEL labelElse → executa elseBlock (se houver)
            // LABEL labelEnd
            IfNode i = (IfNode) node;
            String condition = generateExpr(i.condition);
            String labelElse = newLabel();
            String labelEnd = newLabel();
            instructions.add(new Instruction("IF_FALSE", condition, labelElse, null));
            generate(i.thenBlock);
            instructions.add(new Instruction("GOTO", labelEnd, null, null));
            instructions.add(new Instruction("LABEL", labelElse, null, null));
            if (i.elseBlock != null) {
                generate(i.elseBlock);
            }
            instructions.add(new Instruction("LABEL", labelEnd, null, null));
        }

        else if (node instanceof WhileNode) {
            // Laço sector:
            // LABEL labelStart → avalia condição → IF_FALSE para labelEnd
            // Executa body → GOTO labelStart
            // LABEL labelEnd
            WhileNode w = (WhileNode) node;
            String labelStart = newLabel();
            String labelEnd = newLabel();
            instructions.add(new Instruction("LABEL", labelStart, null, null));
            String condition = generateExpr(w.condition);
            instructions.add(new Instruction("IF_FALSE", condition, labelEnd, null));
            generate(w.body);
            instructions.add(new Instruction("GOTO", labelStart, null, null));
            instructions.add(new Instruction("LABEL", labelEnd, null, null));
        }

        return instructions;
    }

    /**
     * Avalia uma expressão e retorna o nome da variável temporária
     * que contém o resultado. Gera as instruções TAC necessárias
     * para calcular a expressão.
     *
     * @param node nó de expressão da AST
     * @return nome da variável ou temporário que contém o resultado
     */
    private String generateExpr(ASTNode node) {

        if (node instanceof LiteralNode) {
            // Literal (número ou booleano) — cria temporário e atribui o valor
            LiteralNode l = (LiteralNode) node;
            String temp = newTemp();
            instructions.add(new Instruction("ASSIGN", temp, String.valueOf(l.value), null));
            return temp;
        }

        else if (node instanceof IdentifierNode) {
            // Identificador — retorna o nome da variável diretamente
            // sem gerar instrução extra
            IdentifierNode i = (IdentifierNode) node;
            return i.name;
        }

        else if (node instanceof BinOpNode) {
            // Operação binária — avalia os dois lados, cria temporário
            // e gera a instrução com o operador correto
            BinOpNode b = (BinOpNode) node;
            String left = generateExpr(b.left);
            String right = generateExpr(b.right);
            String temp = newTemp();
            String op;
            if (b.operator.equals("+"))       op = "ADD";
            else if (b.operator.equals("-"))  op = "SUB";
            else if (b.operator.equals("*"))  op = "MUL";
            else if (b.operator.equals("/"))  op = "DIV";
            else if (b.operator.equals("==")) op = "EQ";
            else if (b.operator.equals("!=")) op = "NEQ";
            else if (b.operator.equals("<"))  op = "LT";
            else if (b.operator.equals(">"))  op = "GT";
            else throw new RuntimeException("Operador não suportado: " + b.operator);
            instructions.add(new Instruction(op, temp, left, right));
            return temp;
        }

        throw new RuntimeException("Expressão não suportada: " + node.getClass().getSimpleName());
    }
}
