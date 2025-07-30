# Pacote `com.github.rickmvi.jtoolbox.template`

Este pacote oferece utilitários para formatação e conversão segura de dados em aplicações de console.

---

## Classes e Descrições

### 1. TemplateFormatter

Classe utilitária para formatar strings a partir de templates contendo tokens/padrões definidos por expressões regulares.

#### Método principal:

- `String format(String template, Pattern pattern, BiFunction<Matcher, Integer, String> resolver)`

- Recebe um template (string), um padrão regex e uma função `resolver` que, para cada ocorrência encontrada no template, retorna a string que irá substituir o token encontrado.

- A função `resolver` recebe um objeto `Matcher` (com os grupos da regex) e o índice da ocorrência atual, permitindo criar substituições dinâmicas e customizadas.

#### Exemplo de uso:

```java
String template = "Olá, {{nome}}! Hoje é {{dia}}.";

String resultado = TemplateFormatter.format(
    template,
    Pattern.compile("\\{\\{(.*?)}}"),
    (matcher, index) -> {
        String chave = matcher.group(1);
        switch (chave) {
            case "nome": return "Rick";
            case "dia": return "terça-feira";
            default: return "desconhecido";
        }
    }
);

System.out.println(resultado); // Saída: Olá, Rick! Hoje é terça-feira.
```

### 2. TryConvert

Classe utilitária para realizar conversões entre tipos com segurança, evitando exceções e tratando valores nulos.

#### Método principal:

- `<T, R> Optional<R> convert(T value, Function<T, R> converter)`

- Tenta aplicar a função `converter` ao valor `value`.

- Se o valor for `null` ou a conversão falhar (lançar exceção), retorna um `Optional.empty().`

- Caso contrário, retorna o valor convertido dentro de um `Optional`.

- Em caso de erro, registra um log de aviso automaticamente.

#### Exemplo de uso:

```java
Optional<Integer> numero = TryConvert.convert("123", Integer::parseInt);
int valor = numero.orElse(0);
System.out.println(valor); // Saída: 123

Optional<Double> erro = TryConvert.convert("abc", Double::parseDouble);
System.out.println(erro.isPresent()); // Saída: false (conversão falhou)
```
___
### Resumo

- **TemplateFormatter** facilita a manipulação e substituição dinâmica em strings com padrões.

- **TryConvert** torna a conversão de tipos mais robusta e segura, evitando exceções e facilitando o tratamento funcional com `Optional`.
___

### Licença

Ambas as classes são parte da Console API - JToolbox, licenciada sob LGPL v3.