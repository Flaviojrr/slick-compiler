package com.slick.semantic;

import com.slick.parser.ASTNode;
import com.slick.parser.nodes.*;

/**
 * Fase C — Análise Semântica.
 *
 * O SemanticAnalyzer percorre a AST recursivamente verificando
 * a consistência lógica do programa — garantindo que ele "faz sentido"
 * além de apenas ter estrutura gramatical válida.
 *
 * Verificações realizadas:
 * - Variável usada sem ter sido declarada → erro
 * - Variável declarada mais de uma vez → erro
 *
 * Usa uma SymbolTable para registrar e consultar os identificadores
 * declarados ao longo da análise.
 */
public class SemanticAnalyzer {

    /** Tabela de símbolos — registra todas as variáveis e funções declaradas */
    SymbolTable table = new SymbolTable();

    /**
     * Analisa semanticamente um nó da AST.
     * É chamado recursivamente para cada nó filho.
     *
     * @param node nó da AST a analisar
     * @throws Exception se um erro semântico for detectado
     */
    public void analyze(ASTNode node) throws Exception {

        if (node instanceof ProgramNode) {
            // Nó raiz — analisa cada declaração do programa
            ProgramNode p = (ProgramNode) node;
            for (ASTNode decl : p.declarations) analyze(decl);

        } else if (node instanceof FuncDeclNode) {
            // Declaração de função — registra o nome na tabela
            // e analisa o corpo da função
            FuncDeclNode f = (FuncDeclNode) node;
            table.define(new Symbol(f.getName(), "race"));
            analyze(f.getBody());

        } else if (node instanceof BlockNode) {
            // Bloco de código — analisa cada statement dentro do bloco
            BlockNode b = (BlockNode) node;
            for (ASTNode stmt : b.statements) analyze(stmt);

        } else if (node instanceof VarDeclNode) {
            // Declaração de variável — verifica se já foi declarada antes.
            // Se não, registra na tabela e analisa o inicializador (se houver)
            VarDeclNode v = (VarDeclNode) node;
            if (table.contains(v.name)) {
                throw new Exception("Variável '" + v.name + "' já declarada.");
            }
            table.define(new Symbol(v.name, v.type));
            if (v.initializer != null) analyze(v.initializer);

        } else if (node instanceof AssignNode) {
            // Atribuição — verifica se a variável foi declarada antes de receber valor
            AssignNode a = (AssignNode) node;
            if (!table.contains(a.name)) {
                throw new Exception("Variável '" + a.name + "' não declarada.");
            }
            analyze(a.value);

        } else if (node instanceof IdentifierNode) {
            // Uso de identificador em expressão — verifica se foi declarado
            IdentifierNode i = (IdentifierNode) node;
            if (!table.contains(i.name)) {
                throw new Exception("Identificador '" + i.name + "' não declarado.");
            }

        } else if (node instanceof IfNode) {
            // Condicional pit/stay — analisa condição, bloco then e bloco else
            IfNode i = (IfNode) node;
            analyze(i.condition);
            analyze(i.thenBlock);
            if (i.elseBlock != null) analyze(i.elseBlock);

        } else if (node instanceof WhileNode) {
            // Laço sector — analisa condição e corpo do laço
            WhileNode w = (WhileNode) node;
            analyze(w.condition);
            analyze(w.body);

        } else if (node instanceof BinOpNode) {
            // Operação binária — analisa os dois operandos
            BinOpNode b = (BinOpNode) node;
            analyze(b.left);
            analyze(b.right);

        } else if (node instanceof RadioNode) {
            // Impressão — analisa a expressão a ser impressa
            RadioNode r = (RadioNode) node;
            analyze(r.expression);

        } else if (node instanceof ReturnNode) {
            // Retorno de função — analisa o valor retornado (se houver)
            ReturnNode r = (ReturnNode) node;
            if (r.value != null) analyze(r.value);

        } else if (node instanceof LiteralNode) {
            // Literal — sempre válido, nenhuma verificação necessária

        } else if (node instanceof CallNode) {
            // Chamada de função — não verificada na versão atual

        } else if (node instanceof TelemetryNode) {
            // Leitura de entrada — não verificada na versão atual
        }
    }
}
