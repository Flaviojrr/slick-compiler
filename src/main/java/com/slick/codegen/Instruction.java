package com.slick.codegen;

/**
 * Representa uma instrução do Código de Três Endereços (TAC).
 *
 * O TAC é uma representação intermediária do programa, independente
 * de máquina. Cada instrução tem no máximo três operandos:
 * um resultado e dois argumentos.
 *
 * Exemplos de instruções geradas:
 *   ASSIGN  x    10    null   → x = 10
 *   ADD     t0   x     y      → t0 = x + y
 *   PRINT   null t0    null   → imprime t0
 *   LABEL   L0   null  null   → marca o ponto L0 no código
 *   GOTO    L1   null  null   → salta para L1
 *   IF_FALSE t0  L0    null   → se t0 for falso, salta para L0
 */
public class Instruction {

    /** Operação a ser realizada: ASSIGN, ADD, SUB, MUL, DIV, PRINT, READ, LABEL, GOTO, IF_FALSE, RETURN */
    public final String op;

    /** Variável ou label onde o resultado é armazenado. Pode ser null. */
    public final String result;

    /** Primeiro argumento da operação. Pode ser null. */
    public final String arg1;

    /** Segundo argumento da operação. Pode ser null para operações unárias. */
    public final String arg2;

    /**
     * Constrói uma instrução TAC com os quatro campos.
     *
     * @param op     operação a realizar
     * @param result destino do resultado
     * @param arg1   primeiro argumento
     * @param arg2   segundo argumento (pode ser null)
     */
    public Instruction(String op, String result, String arg1, String arg2) {
        this.op = op;
        this.result = result;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }
}
