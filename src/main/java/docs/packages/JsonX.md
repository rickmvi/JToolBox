# JsonX - Biblioteca JSON Configurável para Java

JsonX é uma biblioteca wrapper sobre Gson que fornece uma API fluente, configurável e fácil de usar para trabalhar com JSON em Java.

## 🚀 Características

- ✅ **API Fluente**: Builder pattern para configuração intuitiva
- ✅ **Marshal/Unmarshal**: Conversão bidirecional entre objetos Java e JSON
- ✅ **Suporte a Arquivos**: Leitura e escrita direta de/para arquivos
- ✅ **Navegação JSON**: JsonXPath para navegar estruturas JSON com notação de ponto
- ✅ **Utilitários**: Funções auxiliares para manipulação de JSON
- ✅ **Java 8+ Time API**: Suporte integrado para LocalDate, LocalDateTime, etc.
- ✅ **TypeToken**: Suporte completo para tipos genéricos
- ✅ **Configurável**: Pretty print, serialização de nulls, formato de datas, etc.
- ✅ **Exception Handling**: Exceções customizadas e tratamento de erros

## 📦 Estrutura do Projeto

```
com.github.rickmvi.jtoolbox.json/
├── JsonX.java              // Classe principal
├── JsonXBuilder.java       // Builder para configuração
├── JsonXConfig.java        // Classe de configuração
├── JsonXException.java     // Exceção customizada
├── JsonXUtils.java         // Métodos utilitários
└── JsonXPath.java          // Navegação JSON com paths
```

## 🔧 Instalação

Adicione Gson ao seu `pom.xml`:

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
```

## 📖 Uso Básico

### Marshal (Object → JSON)

```java
// Simples
Person person = new Person("John", 30);
String json = JsonX.toJson(person);

// Com configuração
JsonX jsonx = JsonX.builder()
    .prettyPrint()
    .serializeNulls()
    .build();

String json = jsonx.marshal(person);
```

### Unmarshal (JSON → Object)

```java
// Simples
String json = "{\"name\":\"John\",\"age\":30}";
Person person = JsonX.fromJson(json, Person.class);

// Com genéricos (List, Map, etc)
String json = "[{\"name\":\"John\"},{\"name\":\"Jane\"}]";
List<Person> people = JsonX.parse(json, new TypeToken<List<Person>>(){});
```

### Trabalhar com Arquivos

```java
// Escrever para arquivo
Person person = new Person("John", 30);
JsonX.create().marshalToFile(person, "person.json");

// Ler de arquivo
Person person = JsonX.parseFile("person.json", Person.class);

// Com genéricos
List<Person> people = JsonX.parseFile(
    "people.json", 
    new TypeToken<List<Person>>(){}
);
```

## 🎨 Configuração Avançada

### Builder Fluente

```java
JsonX jsonx = JsonX.builder()
    .prettyPrint()                    // Formatar JSON com indentação
    .serializeNulls()                 // Incluir campos null
    .disableHtmlEscaping()            // Não escapar HTML (<, >, etc)
    .dateFormat("yyyy-MM-dd")         // Formato de datas
    .lenient()                        // Modo leniente (aceita JSON malformado)
    .charsetUtf8()                    // Charset UTF-8
    .fieldNamingPolicy(               // Política de nomes
        FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES
    )
    .withJava8TimeSupport()           // Suporte a LocalDate/LocalDateTime
    .build();
```

### Políticas de Nomes de Campos

```java
// camelCase (padrão Java)
JsonX jsonx = JsonX.builder()
    .fieldNamingPolicy(FieldNamingPolicy.IDENTITY)
    .build();

// snake_case (padrão Python)
JsonX jsonx = JsonX.builder()
    .fieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .build();

// UPPER_CASE_WITH_UNDERSCORES
JsonX jsonx = JsonX.builder()
    .fieldNamingPolicy(FieldNamingPolicy.UPPER_CASE_WITH_UNDERSCORES)
    .build();
```

### TypeAdapters Customizados

```java
// Registrar adapter customizado
JsonX jsonx = JsonX.builder()
    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
    .registerTypeAdapter(Money.class, new MoneyAdapter())
    .build();

// Implementação de adapter
class MoneyAdapter implements JsonSerializer<Money>, JsonDeserializer<Money> {
    @Override
    public JsonElement serialize(Money src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getAmount() + " " + src.getCurrency());
    }

    @Override
    public Money deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        String[] parts = json.getAsString().split(" ");
        return new Money(Double.parseDouble(parts[0]), parts[1]);
    }
}
```

## 🧭 Navegação JSON com JsonXPath

```java
String json = """
{
    "user": {
        "name": "John",
        "age": 30,
        "address": {
            "city": "New York",
            "zipCode": "10001"
        }
    }
}
""";

JsonObject jsonObject = JsonX.create().parse(json).getAsJsonObject();

// Navegação com notação de ponto
String city = JsonXPath.of(jsonObject)
    .get("user.address.city")
    .asString()
    .orElse("Unknown");
// Resultado: "New York"

// Navegação com fallback
int age = JsonXPath.of(jsonObject)
    .get("user.age")
    .asInt()
    .orElse(0);
// Resultado: 30

// Navegação em arrays
String json = """
{
    "users": [
        {"name": "John"},
        {"name": "Jane"},
        {"name": "Bob"}
    ]
}
""";

String secondUserName = JsonXPath.of(jsonObject)
    .get("users")
    .at(1)  // Índice do array
    .asObject()
    .flatMap(obj -> JsonXPath.of(obj).get("name").asString())
    .orElse("Unknown");
// Resultado: "Jane"
```

## 🛠️ Utilitários JsonXUtils

```java
// Converter JsonObject para Map
Map<String, Object> map = JsonXUtils.toMap(jsonObject);

// Converter JsonArray para List
List<Object> list = JsonXUtils.toList(jsonArray);

// Extrair valor com path
String value = JsonXUtils.extractString(jsonObject, "user.name")
    .orElse("Unknown");

Integer age = JsonXUtils.extractInt(jsonObject, "user.age")
    .orElse(0);

// Mesclar dois JsonObjects
JsonObject merged = JsonXUtils.merge(baseObject, overlayObject);

// Verificar se contém chave
boolean hasCity = JsonXUtils.contains(jsonObject, "user.address.city");

// Obter todas as chaves (incluindo aninhadas)
Set<String> allKeys = JsonXUtils.getAllKeys(jsonObject);
// Resultado: ["user", "user.name", "user.age", "user.address", ...]

// Verificar se está vazio
boolean isEmpty = JsonXUtils.isEmpty(jsonObject);
```

## 📅 Suporte a Java 8 Time API

```java
// Configurar com suporte padrão
JsonX jsonx = JsonX.builder()
    .withJava8TimeSupport()
    .build();

// Configurar com formatos customizados
JsonX jsonx = JsonX.builder()
    .withJava8TimeSupport("dd/MM/yyyy", "dd/MM/yyyy HH:mm:ss")
    .build();

// Usar
class Event {
    private LocalDate date;
    private LocalDateTime timestamp;
    
    // constructors, getters, setters
}

Event event = new Event(
    LocalDate.of(2024, 1, 15),
    LocalDateTime.now()
);

String json = jsonx.marshal(event);
// {"date":"2024-01-15","timestamp":"2024-01-15T14:30:00"}

Event parsed = jsonx.unmarshal(json, Event.class);
```

## ✅ Validação de JSON

```java
String validJson = "{\"name\":\"John\",\"age\":30}";
String invalidJson = "{name: test}";

boolean isValid1 = JsonX.validate(validJson);    // true
boolean isValid2 = JsonX.validate(invalidJson);  // false

// Ou com instância
JsonX jsonx = JsonX.create();
boolean isValid = jsonx.isValid(jsonString);
```

## 🎨 Pretty Print e Minify

```java
JsonX jsonx = JsonX.create();

// Pretty print
String compact = "{\"name\":\"John\",\"age\":30}";
String pretty = jsonx.prettify(compact);
/*
{
  "name": "John",
  "age": 30
}
*/

// Minify
String prettyJson = """
{
  "name": "John",
  "age": 30
}
""";
String minified = jsonx.minify(prettyJson);
// {"name":"John","age":30}
```

## 🔄 Conversão entre Tipos

```java
// Object -> JsonElement
Person person = new Person("John", 30);
JsonElement element = jsonx.marshalToTree(person);

// JsonElement -> Object
JsonElement element = jsonx.parse(jsonString);
Person person = jsonx.unmarshal(element, Person.class);

// JsonElement -> Map/List
JsonObject jsonObject = element.getAsJsonObject();
Map<String, Object> map = JsonXUtils.toMap(jsonObject);

JsonArray jsonArray = element.getAsJsonArray();
List<Object> list = JsonXUtils.toList(jsonArray);
```

## 🎯 Exemplos Práticos

### API REST Response

```java
@GetMapping("/api/users")
public String getUsers() {
    List<User> users = userService.getAllUsers();
    
    return JsonX.builder()
        .prettyPrint()
        .serializeNulls()
        .build()
        .marshal(users);
}
```

### Configuração de Aplicação

```java
public class Config {
    public static AppConfig load(String filePath) {
        return JsonX.builder()
            .lenient()
            .build()
            .unmarshalFromFile(filePath, AppConfig.class);
    }
    
    public static void save(AppConfig config, String filePath) {
        JsonX.builder()
            .prettyPrint()
            .build()
            .marshalToFile(config, filePath);
    }
}
```

### Data Transfer Object

```java
class UserDTO {
    private String name;
    private int age;
    private List<String> roles;
    
    public String toJson() {
        return JsonX.toJson(this);
    }
    
    public static UserDTO fromJson(String json) {
        return JsonX.fromJson(json, UserDTO.class);
    }
}
```

### Logging Estruturado

```java
public void logEvent(String eventType, Map<String, Object> data) {
    String json = JsonX.builder()
        .serializeNulls()
        .build()
        .marshal(Map.of(
            "timestamp", Instant.now(),
            "type", eventType,
            "data", data
        ));
    
    logger.info(json);
}
```

## 🚨 Tratamento de Erros

```java
try {
    Person person = JsonX.fromJson(jsonString, Person.class);
} catch (JsonXException e) {
    logger.error("Failed to parse JSON", e);
    // Handle error
}

// Com Optional para evitar exceções
Optional<Person> personOpt = Optional.empty();
try {
    personOpt = Optional.of(JsonX.fromJson(jsonString, Person.class));
} catch (JsonXException e) {
    // Log and continue
}
```

## 🧪 Testes

O projeto inclui testes completos usando JUnit 5. Execute com:

```bash
mvn test
```

## 📝 Licença

Este projeto faz parte do JToolBox e está disponível para uso conforme a licença do projeto principal.

## 🤝 Contribuindo

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou pull requests.

## 📚 Documentação Adicional

### Métodos Disponíveis no JsonX

| Método | Descrição |
|--------|-----------|
| `marshal(Object)` | Converte objeto para JSON string |
| `unmarshal(String, Class)` | Converte JSON para objeto |
| `marshalToFile(Object, Path)` | Escreve JSON em arquivo |
| `unmarshalFromFile(Path, Class)` | Lê JSON de arquivo |
| `parse(String)` | Parse JSON string para JsonElement |
| `parseFile(Path)` | Parse arquivo JSON |
| `isValid(String)` | Valida se string é JSON válido |
| `prettify(String)` | Formata JSON |
| `minify(String)` | Remove espaços do JSON |

### Configurações do Builder

| Configuração | Descrição | Padrão |
|--------------|-----------|--------|
| `prettyPrint()` | Habilita formatação | `false` |
| `serializeNulls()` | Serializa valores null | `false` |
| `disableHtmlEscaping()` | Desabilita escape HTML | `false` |
| `dateFormat(String)` | Formato de datas | Padrão Gson |
| `lenient()` | Modo leniente | `false` |
| `charset(Charset)` | Charset para arquivos | `UTF-8` |
| `withJava8TimeSupport()` | Suporte Java 8 Time | Desabilitado |
