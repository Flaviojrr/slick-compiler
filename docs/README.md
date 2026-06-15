# Compilador SLICK 🏎️

> Inspirado no pneu slick da Fórmula 1 — usado no limite máximo da performance.

SLICK é uma linguagem de programação criada do zero, compilada por um compilador implementado em Java. Arquivos SLICK têm extensão `.slick` e são executados pela **SlickVM** — uma máquina virtual própria desenvolvida junto com o compilador.

---

## Palavras-chave da Linguagem

| Palavra-chave | Significado | Equivalente |
|---------------|-------------|-------------|
| `lap` | tipo inteiro | `int` |
| `flag` | tipo booleano | `boolean` |
| `green` | verdadeiro | `true` |
| `yellow` | falso | `false` |
| `pit` | condicional | `if` |
| `stay` | senão | `else` |
| `sector` | laço | `while` |
| `race` | função | `func` |
| `podium` | retorno | `return` |
| `radio` | imprimir | `print` |
| `telemetry` | ler entrada | `read` |

---

## Exemplo de Programa

```slick
// calcula voltas restantes e verifica se pode continuar
race verificar(lap total, lap feitas) {
    lap restantes;
    restantes = total - feitas;
    podium restantes;
}

lap totalVoltas;
lap voltasFeitas;

telemetry totalVoltas;
telemetry voltasFeitas;

pit (voltasFeitas < totalVoltas) {
    radio verificar(totalVoltas, voltasFeitas);
} stay {
    radio yellow;
}
```

---

## Arquitetura do Compilador

```
arquivo.slick
      |
      v
[A] Lexer              -> tokens
      |
      v
[B] Parser             -> AST (Árvore de Sintaxe Abstrata)
      |
      v
[C] SemanticAnalyzer   -> AST validada + tabela de símbolos
      |
      v
[D] TACGenerator       -> Código de Três Endereços
      |
      v
[E] SlickVM            -> execução e saída
```

| Fase | Pacote | Responsabilidade |
|------|--------|-----------------|
| Léxica | `com.slick.lexer` | Texto → tokens |
| Sintática | `com.slick.parser` | Tokens → AST |
| Semântica | `com.slick.semantic` | Validação de tipos e escopos |
| Geração IR | `com.slick.codegen` | AST → TAC |
| Execução | `com.slick.vm` | Executa o programa na SlickVM |

---

## Estrutura do Projeto

```
Slick/
├── src/main/java/com/slick/
│   ├── Compiler.java
│   ├── lexer/
│   │   ├── Lexer.java
│   │   ├── Token.java
│   │   └── TokenType.java
│   ├── parser/
│   │   ├── ASTNode.java
│   │   ├── ASTPrinter.java
│   │   ├── Parser.java
│   │   └── nodes/
│   │       ├── ProgramNode.java
│   │       ├── BlockNode.java
│   │       ├── IfNode.java
│   │       ├── WhileNode.java
│   │       ├── FuncDeclNode.java
│   │       ├── VarDeclNode.java
│   │       ├── AssignNode.java
│   │       ├── BinOpNode.java
│   │       ├── LiteralNode.java
│   │       ├── IdentifierNode.java
│   │       ├── CallNode.java
│   │       ├── RadioNode.java
│   │       ├── ReturnNode.java
│   │       └── TelemetryNode.java
│   ├── semantic/
│   │   ├── Symbol.java
│   │   ├── SymbolTable.java
│   │   └── SemanticAnalyzer.java
│   ├── codegen/
│   │   ├── Instruction.java
│   │   └── TACGenerator.java
│   └── vm/
│       └── SlickVM.java
└── examples/
    ├── hello.slick
    └── corrida.slick
```

---

## Como Compilar e Executar

### Pré-requisitos
- Java 17+
- Maven 3+

### Compilar
```bash
mvn clean compile
```

### Executar
```bash
java -cp target/classes com.slick.Compiler examples/hello.slick
```

### Saída esperada
```
=== SLICK COMPILER ===

[LEXER]    OK - 27 tokens generated
[PARSER]   OK - AST built
[SEMANTIC] OK - no errors

[TAC] Intermediate code:
-----------------------
ASSIGN  t0   10   null
ASSIGN  x    t0   null
...
-----------------------

[VM] Running...
-----------------------
Output:
15
-----------------------
```

---

## Detecção de Erros

O compilador detecta e reporta três categorias de erro:

**Erro léxico** — caractere inválido:
```
Erro Léxico: caractere inválido '@' na linha 3, coluna 12
```

**Erro sintático** — estrutura inválida:
```
Erro Sintático: esperava ')' mas encontrou ';' na linha 5, coluna 8
```

**Erro semântico** — variável não declarada ou duplicada:
```
Erro Semântico: variável 'voltas' não declarada na linha 7, coluna 5
```

---

## Gramática (BNF)

```
program     ::= declaration*
declaration ::= funcDecl | statement
funcDecl    ::= "race" IDENTIFIER "(" paramList? ")" block
paramList   ::= type IDENTIFIER ( "," type IDENTIFIER )*
type        ::= "lap" | "flag"
statement   ::= ifStmt | whileStmt | returnStmt | radioStmt
              | telemetryStmt | varDecl | assignStmt
ifStmt      ::= "pit" "(" expression ")" block ( "stay" block )?
whileStmt   ::= "sector" "(" expression ")" block
returnStmt  ::= "podium" expression? ";"
radioStmt   ::= "radio" expression ";"
telemetryStmt ::= "telemetry" IDENTIFIER ";"
varDecl     ::= type IDENTIFIER ( "=" expression )? ";"
assignStmt  ::= IDENTIFIER "=" expression ";"
block       ::= "{" statement* "}"
expression  ::= equality
equality    ::= comparison ( ( "==" | "!=" ) comparison )*
comparison  ::= term ( ( "<" | ">" ) term )*
term        ::= factor ( ( "+" | "-" ) factor )*
factor      ::= unary ( ( "*" | "/" ) unary )*
unary       ::= "-" unary | primary
primary     ::= NUMBER | "green" | "yellow"
              | IDENTIFIER ( "(" argList? ")" )?
              | "(" expression ")"
```

---

## Status do Desenvolvimento

- ✅ Fase A — Análise Léxica
- ✅ Fase B — Análise Sintática
- ✅ Fase C — Análise Semântica
- ✅ Fase D — Geração de TAC
- ✅ Fase E — SlickVM

---

## Equipe

- Flávio Cerqueira Santos Júnior
- Guilherme Andrade Matos
- Luiz Fernando Badaró Villas Bôas

Engenharia de Software — UCSAL — Disciplina: Compiladores
