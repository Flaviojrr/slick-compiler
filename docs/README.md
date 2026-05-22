# SLICK 🏎️

> Linguagem de programação inspirada na Fórmula 1 — rápida, precisa e no limite da performance.

**SLICK** é uma linguagem de programação criada do zero, compilada por um compilador implementado em Java. O nome vem do pneu *slick* — o pneu liso de corrida usado nas condições ideais, quando o piloto está no limite máximo da performance. Assim como o pneu slick entrega o melhor resultado nas condições certas, a linguagem SLICK entrega execução rápida e precisa.

Arquivos SLICK têm extensão `.slick` e são compilados para bytecode executado pela **SlickVM** — uma máquina virtual própria desenvolvida junto com o compilador.

---

## 🏁 Sobre a linguagem

| Conceito | SLICK | Equivalente |
|---|---|---|
| Tipo inteiro | `lap` | int |
| Tipo booleano | `flag` | boolean |
| Verdadeiro | `green` | true |
| Falso | `yellow` | false |
| Condicional | `pit` / `stay` | if / else |
| Laço | `sector` | while |
| Função | `race` | func |
| Retorno | `podium` | return |
| Imprimir | `radio` | print |
| Ler entrada | `telemetry` | read |

### Exemplo de programa

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

## ⚙️ Arquitetura do compilador

O compilador é dividido em cinco fases:

```
Código .slick
     ↓
  A. Análise Léxica      →  tokens
     ↓
  B. Análise Sintática   →  AST (Árvore de Sintaxe Abstrata)
     ↓
  C. Análise Semântica   →  AST validada
     ↓
  D. Geração de TAC      →  Código de Três Endereços
     ↓
  E. SlickVM             →  execução do bytecode
```

| Fase | Pacote | Responsabilidade |
|---|---|---|
| Léxica | `com.slick.lexer` | Texto → tokens |
| Sintática | `com.slick.parser` | Tokens → AST |
| Semântica | `com.slick.semantic` | Validação de tipos e escopos |
| Geração IR | `com.slick.codegen` | AST → TAC → Bytecode |
| Execução | `com.slick.vm` | Executa o bytecode na SlickVM |

---

## 📁 Estrutura do projeto

```
slick-compiler/
├── src/
│   └── main/java/com/slick/
│       ├── lexer/
│       │   ├── Token.java
│       │   ├── TokenType.java
│       │   └── Lexer.java
│       ├── parser/
│       │   ├── ASTNode.java
│       │   ├── nodes/          ← subclasses da AST
│       │   ├── Parser.java
│       │   └── ASTPrinter.java
│       ├── semantic/
│       │   ├── Symbol.java
│       │   ├── SymbolTable.java
│       │   └── SemanticAnalyzer.java
│       ├── codegen/
│       │   ├── Instruction.java
│       │   ├── TACGenerator.java
│       │   ├── Opcode.java
│       │   └── BytecodeGenerator.java
│       ├── vm/
│       │   └── SlickVM.java
│       └── Compiler.java
├── examples/
│   ├── hello.slick
│   ├── fatorial.slick
│   └── corrida.slick
├── docs/
│   └── relatorio.md
├── BUGS.md
└── README.md
```

---

## 🚀 Como rodar

### Pré-requisitos
- Java 17 ou superior
- Git

### Clonar o repositório
```bash
git clone https://github.com/SEU_USUARIO/slick-compiler.git
cd slick-compiler
```

### Compilar o projeto
```bash
javac -d out -sourcepath src/main/java src/main/java/com/slick/Compiler.java
```

### Executar um programa .slick
```bash
java -cp out com.slick.Compiler examples/hello.slick
```

### Exemplo de saída
```
$ java -cp out com.slick.Compiler examples/hello.slick
Compilando: hello.slick
[Léxica]   OK — 12 tokens gerados
[Sintática] OK — AST construída
[Semântica] OK — sem erros de tipo
[TAC]       OK — 8 instruções geradas
[SlickVM]   Executando...

Hello from SLICK!
```

---

## 🧪 Testes

O compilador detecta e reporta três categorias de erro:

**Erro léxico** — caractere inválido:
```
Erro Léxico: caractere inválido '@' na linha 3, coluna 12
```

**Erro sintático** — estrutura inválida:
```
Erro Sintático: esperava ')' mas encontrou ';' na linha 5, coluna 8
```

**Erro semântico** — tipo incompatível ou variável não declarada:
```
Erro Semântico: variável 'voltas' não declarada na linha 7, coluna 5
Erro Semântico: operador '+' não suportado entre LAP e FLAG na linha 12
```

---

## 📋 Status do desenvolvimento

- [x] Fase A — Análise Léxica
- [ ] Fase B — Análise Sintática
- [ ] Fase C — Análise Semântica
- [ ] Fase D — Geração de TAC
- [ ] Fase E — SlickVM

---

## 👤 Autor

**Flávio Cerqueira Santos Júnior**  
Engenharia de Software — UCSAL  
Disciplina: Compiladores

---

## 📄 Licença

Projeto acadêmico — todos os direitos reservados.
