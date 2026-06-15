# Compilador SLICK

> Inspirado no pneu slick da Fórmula 1 — usado no limite máximo da performance.

SLICK é uma linguagem de programação compilada desenvolvida como projeto da disciplina de Compiladores da UCSAL (7º semestre). O compilador é implementado em Java e traduz arquivos `.slick` através das cinco fases clássicas de um compilador.

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
lap x;
x = 10;
lap y;
y = 5;
lap resultado;
resultado = x + y;
radio resultado;
```

Saída:
```
15
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
[D] TACGenerator       -> Código de Três Endereços (representação intermediária)
      |
      v
[E] SlickVM            -> execução e saída
```

### Fase A — Análise Léxica
O `Lexer` lê o código fonte caractere por caractere e produz uma lista de tokens. Reconhece palavras reservadas, identificadores, números, operadores e símbolos. Espaços em branco e comentários `//` são ignorados. Teoria aplicada: Expressões Regulares e Autômatos Finitos Determinísticos (AFD).

### Fase B — Análise Sintática
O `Parser` consome a lista de tokens e constrói uma Árvore de Sintaxe Abstrata (AST) usando o método de Descida Recursiva. Cada regra da gramática corresponde a um método dedicado.

### Fase C — Análise Semântica
O `SemanticAnalyzer` percorre a AST e valida a lógica do programa usando uma `SymbolTable`. Detecta variáveis não declaradas e declarações duplicadas.

### Fase D — Geração de Código Intermediário
O `TACGenerator` percorre a AST e gera instruções de Código de Três Endereços (TAC). Cada instrução tem o formato `op resultado arg1 arg2`.

Exemplo de TAC para `resultado = x + y`:
```
ASSIGN  t0          10    null
ASSIGN  x           t0    null
ASSIGN  t1          5     null
ASSIGN  y           t1    null
ADD     t2          x     y
ASSIGN  resultado   t2    null
PRINT   null        resultado  null
```

### Fase E — Execução (SlickVM)
A `SlickVM` interpreta a AST diretamente, executando os statements e avaliando as expressões. As variáveis são armazenadas em um `HashMap<String, Object>`. A VM suporta aritmética, comparações, condicionais, laços, funções e E/S.

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
    └── hello.slick
```

---

## Como Compilar e Executar

### Requisitos
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

[LEXER]    OK - 27 tokens gerados
[PARSER]   OK - AST construída
[SEMANTIC] OK - sem erros

[TAC] Código intermediário:
-----------------------
ASSIGN  t0   10   null
ASSIGN  x    t0   null
...
-----------------------

[VM] Executando...
-----------------------
Saída:
15
-----------------------
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

## Equipe

- Flávio Cerqueira Santos Júnior
- UCSAL — Engenharia de Software — 7º Semestre
- Disciplina: Compiladores