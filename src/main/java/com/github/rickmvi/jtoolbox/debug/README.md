# 🐞 Pacote `debug` - Sistema de Log com Níveis e Cores ANSI

O pacote `debug` fornece uma solução simples e extensível para logs com suporte a múltiplos níveis (como INFO, DEBUG, ERROR etc), formatação colorida via ANSI, timestamp, controle dinâmico de níveis e suporte a mensagens com template.

---

## 📘 Visão Geral

- `Logger`: Logger estático e utilitário com suporte a cores e mensagens parametrizadas.
- `LogLevel`: Enumeração dos níveis de log disponíveis.
- `AnsiColor`: Enumeração de códigos ANSI para colorir a saída no terminal.

---

## ✅ Como usar

### Importação

```java
import com.github.rickmvi.jtoolbox.debug.Logger;
```
---
### 📤 Log básico

```java
info("Aplicação iniciada");
debug("Debugando variáveis...");
warn("Cuidado com valores nulos");
error("Erro inesperado ocorreu");
fatal("Falha crítica detectada");
```
---
### 🧩 Log com placeholders

```java
String user = "alice";
int idade = 30;
info("Usuário: {}, idade: {}", user, idade);
```
---
### 🧨 Log com exceções

#### Método:
```java
public void error(String template, Throwable t, Object... args) { log(LogLevel.ERROR, template, t, args); }
```

#### Como usar:
```java
try {
int result = 10 / 0;
} catch (ArithmeticException e) {
error("Erro ao dividir: {}", e, e.getMessage());
}
```
---
### 🎨 Habilitar cores ANSI (Linux/macOS/Terminais com suporte)

```java
Logger.setUseAnsiColor(true);
info("Mensagem colorida!");
```
---
### 🛠️ Controle de níveis

```java
disable(LogLevel.DEBUG);      // Oculta logs DEBUG
enable(LogLevel.DEBUG);       // Reativa logs DEBUG
disableAll();                 // Desativa todos os níveis
enableAll();                  // Reativa todos (menos OFF)
```
---

### 🧪 Exemplos completos

```java
setUseAnsiColor(true); // Habilita cores (opcional)

trace("Trace com parâmetros: {}, {}", 1, 2);
debug("Debug do sistema");
info("Conexão com sucesso!");
warn("Uso alto de memória");
error("Erro de autenticação");
fatal("Erro irreversível");

Exception e = new RuntimeException("Falha ao carregar recurso");
error("Exceção capturada: {}", e, e.getMessage());
```
___

### 📦 Estrutura de Classes

* `Logger`: Logger estático com métodos para cada nível (`info`, `error`, etc).
* `LogLevel`: Enumeração (`TRACE`, `DEBUG`, ..., `OFF`).
* `AnsiColor`: Mapeia `LogLevel` para códigos ANSI de cor.

___

### 🧰 Dependências necessárias

* `StringFormatter.format(...)`: Template-style string formatter (semelhante a SLF4J).
* `Output.write(...)`: Método utilitário para saída no console.