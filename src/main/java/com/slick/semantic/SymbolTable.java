package com.slick.semantic;

import java.util.HashMap;
import java.util.Map;

/**
 * Tabela de símbolos do compilador SLICK.
 *
 * Armazena todos os identificadores declarados no programa
 * (variáveis e funções) durante a análise semântica.
 *
 * Internamente usa um HashMap onde a chave é o nome do símbolo
 * e o valor é o objeto Symbol correspondente.
 *
 * Usada pelo SemanticAnalyzer para:
 * - Registrar novos identificadores ao serem declarados
 * - Verificar se um identificador já foi declarado (evitar duplicatas)
 * - Verificar se um identificador existe antes de ser usado
 */
public class SymbolTable {

    /** Mapa interno que associa nome do símbolo ao objeto Symbol */
    Map<String, Symbol> table = new HashMap<>();

    /**
     * Registra um novo símbolo na tabela.
     * Se já existir um símbolo com o mesmo nome, ele é sobrescrito.
     *
     * @param s símbolo a ser registrado
     */
    public void define(Symbol s) {
        table.put(s.name, s);
    }

    /**
     * Busca e retorna um símbolo pelo nome.
     *
     * @param name nome do símbolo a buscar
     * @return o Symbol encontrado, ou null se não existir
     */
    public Symbol resolve(String name) {
        if (contains(name)) return table.get(name);
        return null;
    }

    /**
     * Verifica se um símbolo com o nome dado já está registrado na tabela.
     *
     * @param name nome do símbolo a verificar
     * @return true se existir, false caso contrário
     */
    public boolean contains(String name) {
        if (table.containsKey(name)) return true;
        return false;
    }
}
