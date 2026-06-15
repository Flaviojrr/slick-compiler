package com.slick.semantic;

/**
 * Representa um símbolo na tabela de símbolos.
 *
 * Um símbolo é qualquer identificador declarado no programa SLICK:
 * uma variável (lap ou flag) ou uma função (race).
 *
 * É um nó de dados puro — apenas armazena informação,
 * sem lógica ou comportamento.
 */
public class Symbol {

    /** Nome do símbolo — o identificador declarado no código fonte */
    public final String name;

    /** Tipo do símbolo: "lap", "flag" ou "race" (para funções) */
    public final String type;

    /**
     * Constrói um símbolo com nome e tipo.
     *
     * @param name nome do identificador
     * @param type tipo do símbolo
     */
    public Symbol(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
