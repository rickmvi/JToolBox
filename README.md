# Console API - JToolbox

![License](https://img.shields.io/badge/license-GPLv3-blue.svg)

## Visão Geral

A **Console API** é uma biblioteca Java que fornece utilitários para manipulação de console, incluindo:

- Formatação avançada de texto com placeholders nomeados e genéricos
- Leitura segura e simplificada de entrada via Scanner
- Conversão confiável entre strings e tipos primitivos
- Métodos utilitários para saída formatada no console

Ela foi projetada para simplificar operações comuns de I/O no console, oferecendo métodos seguros, claros e flexíveis, adequados para iniciantes e profissionais.

---

## Funcionalidades Principais

- **Formatação de Strings**  
  Suporte avançado para templates com placeholders nomeados (`{{name}}`), genéricos (`{}`) e tokens (`%s`, `%i`).

- **Leitura de Entrada**  
  Wrapper seguro para `Scanner`, com tratamento de erros, suporte a locais (locales) e métodos para leitura de vários tipos primitivos.

- **Conversão de Tipos**  
  Métodos para converter `String` para tipos primitivos (`int`, `long`, `double`, `boolean`), com suporte a valores default e `Optional`.

- **Saída Formatada**  
  Métodos para impressão no console que suportam segurança contra `null`, debugging, impressão formatada e funcional via lambdas.

---

## Dependências

- Java 8 ou superior  
- [Lombok](https://projectlombok.org/) (para reduzir boilerplate code)  
- [JetBrains Annotations](https://www.jetbrains.com/help/idea/nullable-and-notnull-annotations.html) (para anotações de null safety)

---

## Integração

### Maven

[![Maven Central](https://img.shields.io/maven-central/v/io.github.rickmvi/jtoolbox.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.rickmvi/jtoolbox)

Adicione ao seu `pom.xml`:

```xml
<dependency>
  <groupId>io.github.rickmvi</groupId>
  <artifactId>jtoolbox</artifactId>
  <version>1.3.3</version>
</dependency> 
```

### Gradle

Adicione no seu `build.gradle`:

```gradle
dependencies {
    implementation("io.github.rickmvi:jtoolbox:1.3.3")
}
```
___

## Como Usar
### Formatação de Strings

```java
import com.github.rickmvi.formatter.Formatted;
import java.util.HashMap;
import java.util.Map;

// Placeholders nomeados
Map<String, String> values = new HashMap<>();
values.put("name", "John");
values.put("age", "30");
String result = Formatted.replace("Hello {name}, you are {age} years old", values);

// Placeholders genéricos
String formatted = Formatted.format("Hello {}, you are {} years old", "John", 30);

// Placeholders & Tokens(%n, %t, %r, %d)
Out.printFormatted("Test %rHello%d{}, you're {} years old%n", "John", 30)
// Console -> "Hello John, you're 30 years old"
// 

// Tokens de formatação
String tokenFormatted = Formatted.formatTokens("Name: %s, Age: %i", "John", "30");
```

### Leitura de Entrada

```java
import com.github.rickmvi.console.ScannerUtils;
import com.github.rickmvi.console.Location;

// Inicialização
ScannerUtils.init();

// Definir localidade
ScannerUtils.locale(Location.PTBR);

// Leitura de dados
String name = ScannerUtils.next();
int age = ScannerUtils.nextInt();
double salary = ScannerUtils.nextDouble();

// Fechar scanner (opcional)
ScannerUtils.close();
```

### Saída no Console

```java
import com.github.rickmvi.console.Out;

Out.println("Hello World!");
Out.printfSafe("Formatted: %s %d%n", "text", 123);
Out.space();
Out.printDebug("Mensagem de debug");
```

### Conversão de Tipos

```java
import com.github.rickmvi.console.convert.TypeConverter;
import java.util.Optional;

// Conversão segura com Optional
Optional<Integer> number = TypeConverter.convertTo("123", TypeConverter.PrimitiveType.INT);

// Conversão com fallback
int age = (int) TypeConverter.convert("abc", TypeConverter.PrimitiveType.INT, 0);
```

### Exemplo Completo

```java
import com.github.rickmvi.console.Out;
import com.github.rickmvi.console.ScannerUtils;
import com.github.rickmvi.formatter.Formatted;

public class Example {
    public static void main(String[] args) {
        // Inicializa o scanner
        ScannerUtils.init();

        // Solicita entrada do usuário
        Out.println("Enter your name:");
        String name = ScannerUtils.nextLine();

        Out.println("Enter your age:");
        int age = ScannerUtils.nextInt();

        // Formata a saída
        String message = Formatted.format("Hello {}, you are {} years old", name, age);
        Out.println(message);

        // Fecha o scanner
        ScannerUtils.close();
    }
}
```

### 📦 Outros Módulos

- [Pacote `template`](/src/main/java/com/github/rickmvi/jtoolbox/template/README.md): Utilitários para formatação dinâmica de strings e conversão segura com Optional.
- [Pacote `debug`](/src/main/java/com/github/rickmvi/jtoolbox/debug/README.md): Sistema de log extensível com múltiplos níveis (`INFO`, `DEBUG`, `ERROR`, etc), suporte a cores ANSI, mensagens com placeholders, controle dinâmico de visibilidade e tratamento de exceções — ideal para debugging detalhado e visual.
- [Pacote `control`](src/main/java/com/github/rickmvi/jtoolbox/control/README.md): Conjunto de utilitários para controle de fluxo com métodos funcionais e fluentes — inclui ifTrue, switch, repeat, while, e variantes assíncronas usando CompletableFuture. Permite escrita mais expressiva, segura e reutilizável de estruturas de controle, com suporte para cancelamento, encadeamento e mapeamento de retorno.

___

## Padrões de Uso Recomendados

1. **Inicialização**: Sempre chame `ScannerUtils.init()` antes de usar métodos de leitura

2. **Tratamento de erros**: Use os métodos `nextSafe()` e conversões com fallback para evitar exceções

3. **Formatação**: Prefira placeholders nomeados para templates complexos

4. **Localização**: Configure a localização com `ScannerUtils.locale()` quando necessário

## Limitações

  - Não suporta leitura assíncrona de console

  - Formatação de números segue padrão ocidental (ponto decimal)

  - Depende de Lombok para compilação (requer configuração do plugin no IDE)

## Contribuição

Contribuições são muito bem-vindas!
Por favor:

  1. Abra uma issue para reportar bugs ou sugerir melhorias

  2. Fork o repositório

  3. Crie uma branch para sua feature (`git checkout -b minha-feature`)

  4. Faça commit das suas alterações (`git commit -m 'Minha feature'`)

  5. Envie para sua branch (`git push origin minha-feature`)

  5. Abra um Pull Request

## Licença

Este projeto é licenciado sob os termos da **GNU Lesser General Public License v3.0**.  
Você pode usá-lo em projetos comerciais e fechados, desde que preserve os termos da licença.

🔗 [Leia a licença completa aqui](https://www.gnu.org/licenses/lgpl-3.0.html)


**Desenvolvido por Rick M. Viana**

Contato: rickmviana.dev@outlook.com
