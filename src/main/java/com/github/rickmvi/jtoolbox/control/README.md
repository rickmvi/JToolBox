# 🔁 Flow - Utilitário de Controle de Fluxo

A classe `Flow` fornece uma API fluente e funcional para substituir estruturas comuns de controle de fluxo como `if`, `switch`, `for`, `while`, etc., de maneira mais expressiva, reutilizável e encadeável. Também oferece suporte assíncrono com `CompletableFuture` e loops canceláveis.

Parte do pacote [`control`](../control), essa classe é totalmente estática e pode ser usada diretamente sem instância.

---

## 🚀 Recursos Principais

- `ifTrue`, `ifFalse`: Execução condicional funcional.
- `switchOn`, `switchReturn`: Substituto de `switch-case` baseado em mapas.
- `repeat`, `repeatDescending`: Loops com contadores crescentes ou decrescentes.
- `forRange`, `forRangeDescending`: Faixas personalizadas.
- `whileTrue`, `doWhile`: Substituto funcional de `while` e `do-while`.
- Suporte assíncrono: `repeatAsync`, `whileTrueAsync`, `switchReturnAsync`.
- Cancelamento de laços: `repeatCancelable`, `whileTrueCancelable`.

---

## 🧩 Explicação dos Métodos

### 🔀 Condicionais

* `ifTrue(boolean condition, Runnable action)`

    Executa `action` se `condition` for `true`. Substitui `if (cond) {}`.
 

* `ifFalse(boolean condition, Runnable action)`

    Executa `action` se `condition` for `false`. Útil para evitar `if (!cond)`.

<br>

### 🔄 Switch funcional

* `switchOn(K key, Map<K, Runnable> cases, Runnable defaultCase)`

  Executa o `Runnable` correspondente à chave no mapa `cases`, ou `defaultCase` se não existir. Alternativa fluente ao `switch-case`.

* `<T> switchReturn(K key, Map<K, Supplier<T>> cases, Supplier<T> defaultCase)`
  Retorna o valor do `Supplier` associado à chave no mapa, ou do `defaultCase`. Substitui `switch-case` que retorna valor.

* `<T> switchReturnAsync(K key, Map<K, Supplier<T>> cases, Supplier<T> defaultCase)`
  Versão assíncrona de `switchReturn`, retornando um `CompletableFuture<T>`.

<br>

### 🔁 Repetição com contador

* `repeat(int times, IntConsumer action) `

    Executa `action` de 0 até `times - 1`. Similar ao `for (int i = 0; i < times; i++)`.
 
* `repeatDescending(int times, IntConsumer action)`

    Executa `action` de `times - 1` até 0. Útil para iterações regressivas.

* `repeatAsync(int times, IntConsumer action)`

    Versão assíncrona de repeat que retorna CompletableFuture<Void>.

* `repeatCancelable(int times, IntConsumer action, BooleanSupplier cancelCondition)`
Executa o loop até o cancelamento externo (quando `cancelCondition.getAsBoolean()` for `true`).

<br>

### 📐 Intervalos personalizados

* `forRange(int start, int end, IntConsumer action)`

    Executa `action` de `start` até `end - 1`. Permite definir faixas personalizadas crescentes.

* `forRangeDescending(int start, int end, IntConsumer action)`

    Executa `action` de `start` até `end + 1`, regressivamente. Para faixas decrescentes.

<br>

### 🔁 Loops condicionais

* `whileTrue(BooleanSupplier condition, Runnable action)`

    Executa `action` enquanto `condition` for `true`. Alternativa funcional ao `while`.

* `doWhile(BooleanSupplier condition, Runnable action)`

    Executa `action` ao menos uma vez e repete enquanto `condition` for `true`. Equivalente ao `do { } while ()`.

* `whileTrueAsync(BooleanSupplier condition, Runnable action)`

    Versão assíncrona de `whileTrue`, executando em um `CompletableFuture<Void>`.
___

## ✅ Exemplos de Uso

### 🔀 Condicionais

```java
Flow.ifTrue(x > 10, () -> System.out.println("Maior que 10"));
Flow.ifFalse(lista.isEmpty(), () -> System.out.println("Lista não está vazia"));
```
___
### 🔄 Switch funcional com mapa

```java
Map<String, Runnable> cases = Map.of(
    "iniciar", () -> System.out.println("Iniciando..."),
    "parar", () -> System.out.println("Parando...")
);
Flow.switchOn("iniciar", cases, () -> System.out.println("Comando desconhecido"));

Map<Integer, Supplier<String>> results = Map.of(
    1, () -> "um",
    2, () -> "dois"
);
String resultado = Flow.switchReturn(2, results, () -> "desconhecido");
System.out.println(resultado); // "dois"
```
___

### 🔁 Repetições

```java
Flow.repeat(3, i -> System.out.println("Repetição: " + i));
// 0, 1, 2

Flow.repeatDescending(3, i -> System.out.println("Desc: " + i));
// 2, 1, 0
```
___

### 📐 Faixa personalizada

```java
Flow.forRange(5, 10, i -> System.out.print(i + " "));
// 5 6 7 8 9

Flow.forRangeDescending(3, 0, i -> System.out.print(i + " "));
// 3 2 1 0
```
___

### 🔁 Loops while e do-while

```java
AtomicInteger x = new AtomicInteger(0);
Flow.whileTrue(() -> x.get() < 3, () -> {
    System.out.println("x: " + x.getAndIncrement());
});

x.set(0);
Flow.doWhile(() -> x.get() < 3, () -> {
    System.out.println("do-while x: " + x.getAndIncrement());
});
```
___

### ☁️ Assíncrono com CompletableFuture

```java
Flow.repeatAsync(5, i -> System.out.println("Async " + i))
        .thenRun(() -> System.out.println("Fim do async loop"));

Flow.switchReturnAsync("a", Map.of(
        "a", () -> "Letra A"
        ), () -> "Desconhecido")
        .thenAccept(System.out::println);
```
___

### ❌ Cancelamento de Loops

```java
AtomicBoolean cancelar = new AtomicBoolean(false);
new Thread(() -> {
    try { Thread.sleep(100); } catch (InterruptedException ignored) {}
    cancelar.set(true);
}).start();

Flow.repeatCancelable(100, i -> {
    System.out.println("Index: " + i);
}, cancelar::get);
```
___

### 📦 Requisitos

Nenhuma dependência externa além do Java padrão (`java.util.concurrent`, `java.util.function`).

___

### 📚 Conclusão

`Flow` permite reimaginar estruturas clássicas de controle com foco em expressividade, reutilização e integração com programação funcional e assíncrona.