# 🔁 Control - Utilitários de Controle de Fluxo

O pacote [`control`](../control) fornece uma coleção de utilitários fluentes e funcionais para substituir estruturas comuns de controle (`if`, `for`, `while`, `do-while`) de maneira expressiva, reutilizável e até assíncrona.

Essas classes/interfaces permitem escrever código mais funcional, conciso e encadeável, sem perder a clareza.

---

## 🚀 Recursos Principais

- **If** → substitui condicionais (`if/else`) de forma fluente.
- **For** → abstração para laços de repetição (`for`), incluindo faixas (`range`) e coleções (Iterable).
- **While** → substituto funcional para while, com suporte a cancelamento e execução assíncrona.
- **DoWhile** → equivalente funcional ao `do { } while`.

---

## 🧩 Principais Utilitários

### 🔀 Condicionais (`If`)

* `If.runTrue(cond, action).orElse(actionElse)` → executa baseado em `cond`.
* `If.supplyTrue(cond, supplier).orElse(elseSupplier)` → fornece valores condicionais.
* `If.optionalTrue(cond, supplier)` → retorna Optional se verdadeiro.
* `If.trueThrow(cond, exceptionSupplier)` → lança exceção se verdadeiro.
<br>

#### ✅ Exemplo:
```java
If.runTrue(x > 0, () -> System.out.println("Positivo"))
.orElse(() -> System.out.println("Negativo ou zero"));

String msg = If.supplyTrue(nome != null, () -> nome)
.orElse(() -> "Desconhecido");
```
---

### Repetição (`For`)
Interface fluente para iteração sobre intervalos ou coleções.

* `For.range(0, 5).forEach(System.out::println)` → 0,1,2,3,4
* `For.rangeDescentive(5, 0).forEach(System.out::println)` → 5,4,3,2,1,0
* `For.of(lista).anyMatch(x -> x > 10)`
* `For.of("a", "b", "c").findFirst(s -> s.equals("b"))`

#### ✅ Exemplo:
```java
For.range(1, 4).collect(i -> "Item " + i)
.forEach(System.out::println);
// Saída: Item 1, Item 2, Item 3
```
---

### While (`While`)

* `While.runTrue(cond, action)` → executa enquanto `cond` for true.
* `While.trueCancelable(cond, action, cancel)` → com cancelamento externo.
* `While.whileTrueAsync(cond, action)` → versão assíncrona com `CompletableFuture`.

#### ✅ Exemplo:
```java
AtomicInteger i = new AtomicInteger(0);
While.runTrue(() -> i.get() < 3, () -> System.out.println(i.getAndIncrement()));
```
---
### 📐 🔁 DoWhile (`DoWhile`)

* `DoWhile.doWhile(cond, action)` → executa ao menos uma vez.
* `DoWhile.doWhile(supplier, continueCondition)` → usa valores de retorno.

#### ✅ Exemplo:
```java
AtomicInteger x = new AtomicInteger(0);
DoWhile.doWhile(() -> x.get() < 3, () -> System.out.println("x = " + x.getAndIncrement()));
```
___

### 📦 Requisitos

Nenhuma dependência externa além do Java padrão (java.util.concurrent, java.util.function).

Compatível com **Java 8+**.
___

### 📚 Conclusão

O pacote `control` permite reimaginar estruturas clássicas de controle em Java com foco em expressividade, reutilização e integração funcional/assíncrona, sem abrir mão da clareza.