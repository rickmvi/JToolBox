# Sistema de IoC (Inversion of Control) - JToolbox

## Índice

1. [Visão Geral](#visão-geral)
2. [Conceitos Principais](#conceitos-principais)
3. [Inicializando a Aplicação](#inicializando-a-aplicação)
4. [Anotações Principais](#anotações-principais)
5. [Scopo dos Beans](#scopo-dos-beans)
6. [Exemplos de Uso](#exemplos-de-uso)
7. [Ciclo de Vida dos Beans](#ciclo-de-vida-dos-beans)
8. [Injeção de Dependências](#injeção-de-dependências)
9. [Boas Práticas](#boas-práticas)

---

## Visão Geral

O sistema de IoC (Inversion of Control) do JToolbox é um container leve e poderoso que gerencia a criação e configuração de objetos (beans) e suas dependências automaticamente. Este sistema segue os princípios do padrão de inversão de controle, permitindo que você se concentre na lógica de negócios em vez de gerenciar a criação de objetos.

### Características Principais

- ✅ **Auto-detecção de Componentes**: Varredura automática de classes anotadas
- ✅ **Injeção de Dependências**: Suporte para injeção em campos, construtores e parâmetros
- ✅ **Gerenciamento de Ciclo de Vida**: Callbacks `@PostConstruct` e `@PreDestroy`
- ✅ **Múltiplos Escopos**: SINGLETON, PROTOTYPE e escopos customizados
- ✅ **Configuração Baseada em Anotações**: Uso de anotações para simplicidade
- ✅ **Resolução de Ambiguidades**: Qualificadores e anotação `@Primary`
- ✅ **Inicialização Preguiçosa**: Suporte para `@Lazy`

---

## Conceitos Principais

### O que é um Bean?

Um **Bean** é um objeto gerenciado pelo container IoC do JToolbox. O container é responsável por:

- Criar instâncias dos beans
- Injetar dependências
- Gerenciar o ciclo de vida
- Resolver referências entre beans

### ApplicationContext

O `ApplicationContext` é o núcleo do sistema IoC. Ele:

- Mantém o registro de todos os beans disponíveis
- Resolve dependências
- Gerencia o ciclo de vida da aplicação
- Fornece acesso aos beans durante a execução

---

## Inicializando a Aplicação

### Método Básico

A forma mais simples de inicializar uma aplicação JToolbox é usando o método estático `run()`:

```java
public class Application {
    public static void main(String[] args) {
        ApplicationContext context = JToolboxApplication.run(Application.class, args);
    }
}
```

### Com Anotação @ComponentScan

Se seus componentes estão em pacotes diferentes do da classe principal, use `@ComponentScan`:

```java
@ComponentScan(basePackages = {
    "com.myapp.controllers",
    "com.myapp.services",
    "com.myapp.repositories"
})
public class Application {
    public static void main(String[] args) {
        ApplicationContext context = JToolboxApplication.run(Application.class, args);
    }
}
```

### Com Configuração Automática

Para habilitar o servidor web, use:

```java
@EnableWebServer
@ComponentScan(basePackages = {"com.myapp"})
public class Application {
    public static void main(String[] args) {
        ApplicationContext context = JToolboxApplication.run(Application.class, args);
    }
}
```

---

## Anotações Principais

### @Component

A anotação base para marcar uma classe como um componente gerenciado. O container a detectará automaticamente durante a varredura.

```java
@Component
public class MyComponent {
    public void doSomething() {
        System.out.println("Fazendo algo...");
    }
}
```

**Com Nome Customizado:**

```java
@Component("customName")
public class MyComponent {
    // ...
}
```

### @Service

Especialização de `@Component` para classes que contêm lógica de negócios. Semanticamente, indica uma classe de serviço.

```java
@Service("userService")
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public void registerUser(User user) {
        // Validações de negócio
        userRepository.save(user);
    }
}
```

### @Repository

Especialização de `@Component` para classes de acesso a dados. Indica que a classe é responsável pela persistência.

```java
@Repository("userRepository")
public class UserRepository {
    
    @Autowired
    private Jdbc jdbc;
    
    public void save(User user) {
        // Lógica de persistência
        jdbc.execute("INSERT INTO users ...", user);
    }
}
```

### @Configuration

Marca uma classe como uma classe de configuração que pode conter definições de beans através de métodos anotados com `@Bean`.

```java
@Configuration
public class AppConfig {
    
    @Bean
    public DataSource dataSource() {
        return new DataSource(
            "jdbc:mysql://localhost:3306/mydb",
            "user",
            "password"
        );
    }
}
```

### @RestController

Especialização de `@Component` para controladores REST. As classes anotadas lidam com requisições HTTP.

```java
@RestController("/api/v1/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") String id) {
        return userService.findById(id);
    }
}
```

### @Bean

Anotação de método usada em classes `@Configuration` para definir um bean. Útil para integrar bibliotecas de terceiros.

```java
@Configuration
public class DatabaseConfig {
    
    @Bean(value = "mySqlConnection", scope = Component.Scope.SINGLETON)
    public Connection createConnection() {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/db");
    }
}
```

---

## Scopo dos Beans

O escopo define como o container gerencia instâncias de um bean.

### SINGLETON (Padrão)

Uma única instância é criada e compartilhada por toda a aplicação.

```java
@Component(scope = Component.Scope.SINGLETON)
public class DatabaseConnection {
    // Apenas uma instância para toda a aplicação
}
```

### PROTOTYPE

Uma nova instância é criada cada vez que o bean é solicitado.

```java
@Component(scope = Component.Scope.PROTOTYPE)
public class RequestContext {
    // Uma nova instância para cada requisição/chamada
}
```

---

## Exemplos de Uso

### Exemplo 1: Arquitetura em Camadas

Vamos criar uma aplicação de gerenciamento de usuários:

#### 1. Entidade (Model)

```java
public class User {
    private String id;
    private String name;
    private String email;
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

#### 2. Repositório (Camada de Dados)

```java
@Repository("userRepository")
public class UserRepository {
    
    @Autowired
    private Jdbc jdbc;
    
    public User findById(String id) {
        return jdbc.queryForObject(
            "SELECT * FROM users WHERE id = ?",
            new Object[]{id},
            User.class
        );
    }
    
    public void save(User user) {
        jdbc.update(
            "INSERT INTO users (id, name, email) VALUES (?, ?, ?)",
            new Object[]{user.getId(), user.getName(), user.getEmail()}
        );
    }
}
```

#### 3. Serviço (Lógica de Negócios)

```java
@Service("userService")
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User getUserById(String id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        return user;
    }
    
    public void registerUser(String name, String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Email inválido");
        }
        
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName(name);
        user.setEmail(email);
        
        userRepository.save(user);
    }
    
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }
}
```

#### 4. Controlador (Camada de Apresentação)

```java
@RestController("/api/v1/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") String id) {
        return userService.getUserById(id);
    }
    
    @PostMapping
    public void createUser(@RequestBody User user) {
        userService.registerUser(user.getName(), user.getEmail());
    }
}
```

#### 5. Classe Principal

```java
@ComponentScan(basePackages = {
    "com.myapp.repositories",
    "com.myapp.services",
    "com.myapp.controllers"
})
@EnableWebServer
public class Application {
    public static void main(String[] args) {
        ApplicationContext context = JToolboxApplication.run(Application.class, args);
    }
}
```

### Exemplo 2: Configuração com @Bean

Para integrar bibliotecas externas, use `@Bean`:

```java
@Configuration
public class ExternalLibraryConfig {
    
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }
    
    @Bean("mongoConnection")
    public MongoClient mongoClient() {
        return new MongoClient("localhost", 27017);
    }
    
    @Bean(scope = Component.Scope.PROTOTYPE)
    public Logger createLogger() {
        return Logger.getLogger("MyApp");
    }
}
```

### Exemplo 3: Resolvendo Ambiguidades com @Qualifier

Quando há múltiplas implementações da mesma interface:

```java
public interface PaymentService {
    void pay(double amount);
}

@Component("creditCardPayment")
public class CreditCardPaymentService implements PaymentService {
    @Override
    public void pay(double amount) {
        System.out.println("Pagando com cartão: $" + amount);
    }
}

@Component("paypalPayment")
public class PayPalPaymentService implements PaymentService {
    @Override
    public void pay(double amount) {
        System.out.println("Pagando com PayPal: $" + amount);
    }
}

@Service
public class OrderService {
    
    @Autowired
    @Qualifier("paypalPayment")
    private PaymentService paymentService;
    
    public void checkout(double amount) {
        paymentService.pay(amount);
    }
}
```

### Exemplo 4: Injeção por Nome com @Inject

```java
@Component
public class DataProcessor {
    
    @Inject("primaryDataSource")
    private DataSource dataSource;
    
    public void process() {
        // Usa o dataSource específico
    }
}
```

---

## Ciclo de Vida dos Beans

### @PostConstruct

Método executado automaticamente APÓS a injeção de dependências. Útil para inicializações.

```java
@Component
public class DatabaseService {
    
    @Autowired
    private Config config;
    
    private Connection connection;
    
    @PostConstruct
    public void init() {
        try {
            this.connection = DriverManager.getConnection(
                config.getDatabaseUrl(),
                config.getUsername(),
                config.getPassword()
            );
            System.out.println("Conexão com banco de dados estabelecida!");
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao conectar ao banco", e);
        }
    }
}
```

### @PreDestroy

Método executado quando a aplicação está sendo encerrada. Útil para limpeza de recursos.

```java
@Component
public class ConnectionPool {
    
    private List<Connection> connections;
    
    @PostConstruct
    public void init() {
        connections = new ArrayList<>();
        // Inicializa conexões
    }
    
    @PreDestroy
    public void shutdown() {
        for (Connection conn : connections) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
        System.out.println("Pool de conexões encerrado!");
    }
}
```

---

## Injeção de Dependências

### 1. Injeção em Campo (Field Injection)

```java
@Component
public class MyService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User getUser(String id) {
        return userRepository.findById(id);
    }
}
```

### 2. Injeção em Construtor (Constructor Injection)

Recomendado para dependências obrigatórias:

```java
@Component
public class MyService {
    
    private UserRepository userRepository;
    
    @Autowired
    public MyService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User getUser(String id) {
        return userRepository.findById(id);
    }
}
```

### 3. Injeção Opcional

Para dependências não obrigatórias:

```java
@Component
public class MyService {
    
    @Autowired(required = false)
    private OptionalService optionalService;
    
    public void doSomething() {
        if (optionalService != null) {
            optionalService.execute();
        }
    }
}
```

### 4. Injeção por Nome com @Inject

```java
@Component
public class MyService {
    
    @Inject("specificImplementation")
    private MyInterface myInterface;
}
```

---

## Inicialização Preguiçosa com @Lazy

Use `@Lazy` para adiar a criação de um bean até que ele seja realmente necessário:

```java
@Lazy
@Component
public class HeavyService {
    
    public HeavyService() {
        System.out.println("Inicializando HeavyService (pode ser demorado)");
        // Operações caras de inicialização
    }
}

@Component
public class Application {
    
    @Autowired
    private HeavyService heavyService; // Criado somente quando solicitado
    
    public void useService() {
        heavyService.doSomething();
    }
}
```

---

## Anotações Adicionais

### @Primary

Marca um bean como o candidato preferido quando há múltiplas opções:

```java
@Primary
@Component
public class DefaultPaymentService implements PaymentService {
    // Este será injetado por padrão
}

@Component
public class AlternativePaymentService implements PaymentService {
    // Este seria injetado se não houvesse @Primary
}
```

### @Order

Define a ordem de inicialização de componentes:

```java
@Component
@Order(1)
public class FirstComponent {
    // Inicializado primeiro
}

@Component
@Order(2)
public class SecondComponent {
    // Inicializado segundo
}
```

### @Value

Injeta valores de propriedades:

```java
@Component
public class AppConfig {
    
    @Value("${app.name:MyApp}")
    private String appName;
    
    @Value("${app.port:8080}")
    private int port;
}
```

### @ConditionalOnClass

Inicializa o bean apenas se uma classe estiver no classpath:

```java
@Component
@ConditionalOnClass("org.springframework.web.servlet.DispatcherServlet")
public class WebConfiguration {
    // Criado apenas se o servlet estiver disponível
}
```

### @ConditionalOnProperty

Inicializa o bean apenas se uma propriedade tiver um valor específico:

```java
@Component
@ConditionalOnProperty(name = "feature.cache.enabled", havingValue = "true")
public class CacheService {
    // Criado apenas se a propriedade estiver ativada
}
```

---

## Boas Práticas

### 1. Use Injeção em Construtor para Dependências Obrigatórias

```java
// ❌ Não recomendado
@Component
public class UserService {
    @Autowired
    private UserRepository repository;
}

// ✅ Recomendado
@Component
public class UserService {
    private final UserRepository repository;
    
    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}
```

### 2. Use Interfaces para Desacoplamento

```java
// ✅ Bom
public interface UserRepository {
    User findById(String id);
}

@Component
public class JdbcUserRepository implements UserRepository {
    // Implementação específica
}

@Service
public class UserService {
    @Autowired
    private UserRepository repository; // Desacoplado da implementação
}
```

### 3. Qualifique Ambiguidades Explicitamente

```java
// ✅ Bom
@Autowired
@Qualifier("paypalPayment")
private PaymentService paymentService;

// Melhor ainda
@Autowired
private PaymentService paypalPayment; // Nome da variável desambigua
```

### 4. Use @ComponentScan Explicitamente

```java
// ✅ Recomendado
@ComponentScan(basePackages = {
    "com.myapp.services",
    "com.myapp.repositories",
    "com.myapp.controllers"
})
public class Application {
}
```

### 5. Limite o Escopo PROTOTYPE

Use PROTOTYPE apenas quando necessário. Na maioria dos casos, SINGLETON é a escolha correta:

```java
// ❌ Evite
@Component(scope = Component.Scope.PROTOTYPE)
public class UserService {
    // Cria uma nova instância a cada injeção
}

// ✅ Preferir
@Component(scope = Component.Scope.SINGLETON)
public class UserService {
    // Uma única instância compartilhada
}
```

### 6. Implemente Limpeza em @PreDestroy

```java
// ✅ Bom
@Component
public class ResourceManager {
    
    private Resource resource;
    
    @PostConstruct
    public void initialize() {
        this.resource = acquireResource();
    }
    
    @PreDestroy
    public void cleanup() {
        if (resource != null) {
            resource.close();
        }
    }
}
```

### 7. Evite Referências Circulares

Se houver referências circulares, use `@Lazy`:

```java
@Component
public class ServiceA {
    @Autowired
    private ServiceB serviceB;
}

@Component
public class ServiceB {
    @Lazy
    @Autowired
    private ServiceA serviceA; // Injetado preguiçosamente para evitar ciclo
}
```

---

## Resumo das Anotações

| Anotação | Uso | Escopo |
|----------|-----|--------|
| `@Component` | Marca classe como bean | Classe |
| `@Service` | Bean de lógica de negócios | Classe |
| `@Repository` | Bean de acesso a dados | Classe |
| `@RestController` | Controlador REST | Classe |
| `@Configuration` | Classe de configuração | Classe |
| `@Bean` | Define um bean em método | Método |
| `@Autowired` | Injeta dependência por tipo | Campo/Construtor/Parâmetro |
| `@Inject` | Injeta por nome | Campo/Parâmetro |
| `@Qualifier` | Resolve ambiguidade | Campo/Parâmetro |
| `@Primary` | Define preferência | Classe |
| `@Lazy` | Inicialização preguiçosa | Classe/Método |
| `@PostConstruct` | Inicialização pós-injeção | Método |
| `@PreDestroy` | Limpeza antes da destruição | Método |
| `@Value` | Injeta valor de propriedade | Campo |
| `@Order` | Define ordem de inicialização | Classe |

---

## Conclusão

O sistema de IoC do JToolbox oferece uma forma elegante e poderosa de gerenciar dependências e o ciclo de vida dos componentes da sua aplicação. Seguindo as boas práticas e compreendendo os conceitos fundamentais, você pode construir aplicações bem estruturadas e fáceis de manter.

Para mais informações, consulte a documentação da classe `JToolboxApplication` e das anotações disponíveis no pacote `com.github.rickmvi.jtoolbox.ioc.annotations`.

