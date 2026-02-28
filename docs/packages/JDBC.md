# API JDBC - JToolbox

## Índice

1. [Visão Geral](#visão-geral)
2. [Conceitos Principais](#conceitos-principais)
3. [Suporte de Bancos de Dados](#suporte-de-bancos-de-dados)
4. [Construindo URLs de Conexão](#construindo-urls-de-conexão)
5. [JdbcTemplate](#jdbctemplate)
6. [NamedParameterJdbcTemplate](#namedparameterjdbctemplate)
7. [Row Mappers](#row-mappers)
8. [Exemplos Práticos](#exemplos-práticos)
9. [Tratamento de Erros](#tratamento-de-erros)
10. [Boas Práticas](#boas-práticas)

---

## Visão Geral

A API JDBC do JToolbox é um wrapper moderno sobre JDBC puro que simplifica as operações de banco de dados. Ela reduz boilerplate code, fornece convenções sensatas e mantém a flexibilidade do JDBC tradicional.

### Características Principais

- ✅ **Suporte Múltiplos Bancos**: PostgreSQL, MySQL, SQLite, Oracle, SQL Server, H2
- ✅ **Builder Pattern para URLs**: Construção segura de URLs JDBC
- ✅ **JdbcTemplate**: Operações comuns simplificadas
- ✅ **Named Parameters**: Suporte para parâmetros nomeados em SQL
- ✅ **Row Mappers**: Mapeamento automático de resultados
- ✅ **Transações**: Suporte para transações explícitas
- ✅ **Batch Operations**: Operações em lote
- ✅ **Type-Safe**: Conversão automática de tipos

---

## Conceitos Principais

### DataSource

Um `DataSource` é a interface padrão Java para obter conexões com o banco de dados:

```java
DataSource dataSource = // Obter do IoC container ou criar
JdbcTemplate template = new JdbcTemplate(dataSource);
```

### JdbcTemplate

O `JdbcTemplate` encapsula operações JDBC comuns e elimina código repetitivo:

- `update()` - Para INSERT, UPDATE, DELETE
- `query()` - Para SELECT com múltiplas linhas
- `queryForObject()` - Para SELECT com uma linha
- `insertAndReturnKey()` - Para INSERT e obter ID gerado
- `batchUpdate()` - Para operações em lote
- `inTransaction()` - Para transações explícitas

### RowMapper

A interface `RowMapper<T>` é uma função que converte um `ResultSet` em um objeto de tipo `T`:

```java
@FunctionalInterface
public interface RowMapper<T> {
    T mapRow(ResultSet rs) throws SQLException;
}
```

---

## Suporte de Bancos de Dados

O JToolbox suporta os seguintes bancos de dados:

| Banco de Dados | Enum | Driver |
|---|---|---|
| PostgreSQL | `DatabaseType.POSTGRESQL` | `org.postgresql.Driver` |
| MySQL | `DatabaseType.MYSQL` | `com.mysql.cj.jdbc.Driver` |
| SQLite | `DatabaseType.SQLITE` | `org.sqlite.JDBC` |
| Oracle | `DatabaseType.ORACLE` | `oracle.jdbc.OracleDriver` |
| SQL Server | `DatabaseType.SQLSERVER` | `com.microsoft.sqlserver.jdbc.SQLServerDriver` |
| H2 | `DatabaseType.H2` | `org.h2.Driver` |

---

## Construindo URLs de Conexão

### URLBuilder

O `URLBuilder` fornece uma forma segura e fluente de construir URLs JDBC:

#### Construir URL Manualmente

```java
// Com host e porta customizados
String url = URLBuilder.instance(DatabaseType.POSTGRESQL)
    .host("db.example.com")
    .port(5432)
    .database("mydb")
    .build();
// Resultado: jdbc:postgresql://db.example.com:5432/mydb
```

#### Usar Localhost com Portas Padrão

```java
// PostgreSQL com localhost e porta padrão (5432)
String pgUrl = URLBuilder.postgresDataBase("mydb");
// jdbc:postgresql://localhost:5432/mydb

// MySQL com localhost e porta padrão (3306)
String mysqlUrl = URLBuilder.mysqlDataBase("mydb");
// jdbc:mysql://localhost:3306/mydb

// SQL Server
String sqlServerUrl = URLBuilder.sqlServerDataBase("mydb");
// jdbc:sqlserver://localhost:1433/mydb

// Oracle
String oracleUrl = URLBuilder.oracleDataBase("mydb");
// jdbc:oracle:thin:@localhost:1521/mydb
```

#### URL Bruta

Se você tiver uma URL customizada:

```java
String url = URLBuilder.raw("jdbc:postgresql://custom-host:9999/special_db").build();
```

### Jdbc.Builder

A classe `Jdbc` fornece um builder fluente para criar conexões:

#### Com DatabaseType

```java
// PostgreSQL
Connection conn = Jdbc.url(DatabaseType.POSTGRESQL)
    .host("localhost")
    .port(5432)
    .database("mydb")
    .user("postgres")
    .password("password123")
    .connect();

// MySQL
Connection conn = Jdbc.url(DatabaseType.MYSQL)
    .host("localhost")
    .port(3306)
    .database("myapp")
    .user("root")
    .password("secret")
    .connect();
```

#### Com String (Auto-detecção)

```java
// Detecta o banco pelo prefixo da URL
Connection conn = Jdbc.url("jdbc:postgresql://localhost:5432/mydb")
    .map(builder -> builder.user("user").password("pass"))
    .map(builder -> builder.connect())
    .orElseThrow(() -> new RuntimeException("Invalid URL"));
```

#### Sem Credenciais

```java
// Para bancos que não necessitam de usuário/senha (ex: SQLite)
Connection conn = Jdbc.url(DatabaseType.SQLITE)
    .database("myapp.db")
    .connect();
```

---

## JdbcTemplate

### INSERT, UPDATE, DELETE

#### Update Simples

```java
@Repository
public class UserRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // UPDATE
    public void updateUserName(String userId, String newName) {
        int affectedRows = jdbcTemplate.update(
            "UPDATE users SET name = ? WHERE id = ?",
            newName,
            userId
        );
        System.out.println("Linhas afetadas: " + affectedRows);
    }
    
    // DELETE
    public void deleteUser(String userId) {
        jdbcTemplate.update(
            "DELETE FROM users WHERE id = ?",
            userId
        );
    }
}
```

#### Insert e Obter ID Gerado

```java
public Long createUser(String name, String email) {
    long userId = jdbcTemplate.insertAndReturnKey(
        "INSERT INTO users (name, email) VALUES (?, ?)",
        name,
        email
    );
    System.out.println("Novo usuário criado com ID: " + userId);
    return userId;
}
```

### SELECT - Múltiplas Linhas

```java
public List<User> getAllUsers() {
    return jdbcTemplate.query(
        "SELECT id, name, email FROM users",
        rs -> new User(rs.getString("id"), rs.getString("name"), rs.getString("email"))
    );
}

public List<User> getUsersByNamePrefix(String prefix) {
    return jdbcTemplate.query(
        "SELECT id, name, email FROM users WHERE name LIKE ?",
        rs -> new User(rs.getString("id"), rs.getString("name"), rs.getString("email")),
        prefix + "%"
    );
}
```

### SELECT - Uma Única Linha

```java
public Optional<User> getUserById(String userId) {
    return jdbcTemplate.queryForObject(
        "SELECT id, name, email FROM users WHERE id = ?",
        rs -> new User(rs.getString("id"), rs.getString("name"), rs.getString("email")),
        userId
    );
}

public User getUserByIdOrThrow(String userId) {
    return getUserById(userId)
        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userId));
}
```

### Batch Operations (Operações em Lote)

```java
public void insertMultipleUsers(List<User> users) {
    List<Object[]> batchParams = users.stream()
        .map(user -> new Object[]{user.getId(), user.getName(), user.getEmail()})
        .toList();
    
    int[] results = jdbcTemplate.batchUpdate(
        "INSERT INTO users (id, name, email) VALUES (?, ?, ?)",
        batchParams
    );
    
    System.out.println("Usuários inseridos: " + results.length);
}
```

### Transações Explícitas

```java
public void transferBalance(String fromUserId, String toUserId, double amount) {
    jdbcTemplate.inTransaction(conn -> {
        try (Statement stmt = conn.createStatement()) {
            // Deduz do usuário origem
            stmt.executeUpdate(String.format(
                "UPDATE accounts SET balance = balance - %f WHERE user_id = '%s'",
                amount,
                fromUserId
            ));
            
            // Adiciona ao usuário destino
            stmt.executeUpdate(String.format(
                "UPDATE accounts SET balance = balance + %f WHERE user_id = '%s'",
                amount,
                toUserId
            ));
        }
        return null;
    });
}
```

### Execute com PreparedStatementSetter

Para controle mais fino sobre os parâmetros:

```java
public void customExecute() {
    jdbcTemplate.execute(
        "INSERT INTO logs (message, timestamp) VALUES (?, ?)",
        ps -> {
            ps.setString(1, "Evento importante");
            ps.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
        }
    );
}
```

---

## NamedParameterJdbcTemplate

O `NamedParameterJdbcTemplate` permite usar parâmetros nomeados em vez de `?`:

### Sintaxe Básica

```java
@Repository
public class UserRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private NamedParameterJdbcTemplate namedTemplate;
    
    @PostConstruct
    public void init() {
        this.namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }
    
    // UPDATE com parâmetros nomeados
    public void updateUser(User user) {
        Map<String, Object> params = Map.of(
            "id", user.getId(),
            "name", user.getName(),
            "email", user.getEmail()
        );
        
        namedTemplate.update(
            "UPDATE users SET name = :name, email = :email WHERE id = :id",
            params
        );
    }
    
    // SELECT com parâmetros nomeados
    public List<User> searchUsers(String namePattern, String emailDomain) {
        Map<String, Object> params = Map.of(
            "namePattern", namePattern + "%",
            "emailDomain", "%" + emailDomain
        );
        
        return namedTemplate.query(
            "SELECT id, name, email FROM users WHERE name LIKE :namePattern AND email LIKE :emailDomain",
            params,
            rs -> new User(rs.getString("id"), rs.getString("name"), rs.getString("email"))
        );
    }
    
    // SELECT com resultado único
    public Optional<User> findByEmail(String email) {
        Map<String, Object> params = Map.of("email", email);
        
        return namedTemplate.queryForObject(
            "SELECT id, name, email FROM users WHERE email = :email",
            params,
            rs -> new User(rs.getString("id"), rs.getString("name"), rs.getString("email"))
        );
    }
}
```

### Vantagens

- ✅ **Legibilidade**: SQL mais claro e legível
- ✅ **Manutenibilidade**: Fácil identificar quais parâmetros são necessários
- ✅ **Segurança**: Menos propenso a erros de ordem de parâmetros

---

## Row Mappers

### Implementação Manual

```java
// RowMapper inline usando lambda
RowMapper<User> userMapper = rs -> new User(
    rs.getString("id"),
    rs.getString("name"),
    rs.getString("email")
);

List<User> users = jdbcTemplate.query(
    "SELECT id, name, email FROM users",
    userMapper
);
```

### BeanPropertyRowMapper

Mapeia automaticamente colunas do ResultSet para propriedades da classe:

```java
public class User {
    private String id;
    private String name;
    private String email;
    
    // Construtor padrão (necessário)
    public User() {}
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

// Usar BeanPropertyRowMapper
List<User> users = jdbcTemplate.query(
    "SELECT id, name, email FROM users",
    new BeanPropertyRowMapper<>(User.class)
);
```

### Construtor com ResultSet

Se a classe tem um construtor que recebe `ResultSet`, ele será usado automaticamente:

```java
public class User {
    private String id;
    private String name;
    private String email;
    
    // Construtor que recebe ResultSet
    public User(ResultSet rs) throws SQLException {
        this.id = rs.getString("id");
        this.name = rs.getString("name");
        this.email = rs.getString("email");
    }
    
    // Getters...
}

// BeanPropertyRowMapper usará esse construtor
List<User> users = jdbcTemplate.query(
    "SELECT id, name, email FROM users",
    new BeanPropertyRowMapper<>(User.class)
);
```

---

## Exemplos Práticos

### Exemplo 1: CRUD Completo

```java
@Repository
public class ProductRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // CREATE
    public long createProduct(String name, double price, String description) {
        return jdbcTemplate.insertAndReturnKey(
            "INSERT INTO products (name, price, description) VALUES (?, ?, ?)",
            name,
            price,
            description
        );
    }
    
    // READ - Um produto
    public Optional<Product> getProductById(long id) {
        return jdbcTemplate.queryForObject(
            "SELECT id, name, price, description FROM products WHERE id = ?",
            rs -> new Product(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("description")
            ),
            id
        );
    }
    
    // READ - Todos os produtos
    public List<Product> getAllProducts() {
        return jdbcTemplate.query(
            "SELECT id, name, price, description FROM products",
            rs -> new Product(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("description")
            )
        );
    }
    
    // UPDATE
    public void updateProduct(long id, String name, double price) {
        jdbcTemplate.update(
            "UPDATE products SET name = ?, price = ? WHERE id = ?",
            name,
            price,
            id
        );
    }
    
    // DELETE
    public void deleteProduct(long id) {
        jdbcTemplate.update(
            "DELETE FROM products WHERE id = ?",
            id
        );
    }
    
    // Busca com filtros
    public List<Product> searchByPriceRange(double minPrice, double maxPrice) {
        return jdbcTemplate.query(
            "SELECT id, name, price, description FROM products WHERE price BETWEEN ? AND ? ORDER BY price",
            rs -> new Product(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("description")
            ),
            minPrice,
            maxPrice
        );
    }
}
```

### Exemplo 2: Com Serviço e Transações

```java
@Service
public class OrderService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ProductRepository productRepository;
    
    public long createOrder(String userId, List<OrderItem> items) {
        // Transação: criar ordem e seus itens
        return jdbcTemplate.inTransaction(conn -> {
            // Criar a ordem
            long orderId;
            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO orders (user_id, total_price, status) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, userId);
                ps.setDouble(2, calculateTotal(items));
                ps.setString(3, "PENDING");
                ps.executeUpdate();
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    orderId = rs.getLong(1);
                }
            }
            
            // Criar items da ordem
            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)"
            )) {
                for (OrderItem item : items) {
                    ps.setLong(1, orderId);
                    ps.setLong(2, item.getProductId());
                    ps.setInt(3, item.getQuantity());
                    ps.setDouble(4, item.getPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            
            return orderId;
        });
    }
    
    private double calculateTotal(List<OrderItem> items) {
        return items.stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();
    }
}
```

### Exemplo 3: Com Named Parameters

```java
@Repository
public class ReportRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private NamedParameterJdbcTemplate namedTemplate;
    
    @PostConstruct
    public void init() {
        this.namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }
    
    public List<SalesReport> generateSalesReport(
        LocalDate startDate,
        LocalDate endDate,
        String productCategory
    ) {
        Map<String, Object> params = Map.of(
            "startDate", java.sql.Date.valueOf(startDate),
            "endDate", java.sql.Date.valueOf(endDate),
            "category", productCategory
        );
        
        return namedTemplate.query(
            """
            SELECT 
                p.id,
                p.name,
                p.category,
                COUNT(oi.id) as total_items,
                SUM(oi.quantity) as total_quantity,
                SUM(oi.price * oi.quantity) as total_revenue
            FROM products p
            LEFT JOIN order_items oi ON p.id = oi.product_id
            LEFT JOIN orders o ON oi.order_id = o.id
            WHERE p.category = :category
                AND o.created_at BETWEEN :startDate AND :endDate
            GROUP BY p.id, p.name, p.category
            ORDER BY total_revenue DESC
            """,
            params,
            rs -> new SalesReport(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getInt("total_items"),
                rs.getInt("total_quantity"),
                rs.getDouble("total_revenue")
            )
        );
    }
}
```

---

## Tratamento de Erros

### DataAccessException

Todas as operações JDBC do JToolbox lançam `DataAccessException`:

```java
try {
    User user = getUserById("invalid-id")
        .orElseThrow(() -> new DataAccessException("Usuário não encontrado"));
} catch (DataAccessException e) {
    System.err.println("Erro de acesso ao banco: " + e.getMessage());
    // Fazer rollback ou tratar apropriadamente
}
```

### Em Transações

Se um erro ocorrer dentro de uma transação, o rollback é executado automaticamente:

```java
public void riskyOperation() {
    try {
        jdbcTemplate.inTransaction(conn -> {
            // ... operações de banco ...
            if (someCondition) {
                throw new IllegalStateException("Condição não atendida");
            }
            return null;
        });
    } catch (RuntimeException e) {
        // Transação foi feita rollback automaticamente
        System.err.println("Operação falhou e foi revertida: " + e.getMessage());
    }
}
```

---

## Boas Práticas

### 1. Use Injeção de Dependência

```java
// ✅ Recomendado
@Repository
public class UserRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
}

// ❌ Não recomendado
public class UserRepository {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(/* ... */);
}
```

### 2. Use NamedParameters para SQL Complexo

```java
// ✅ Recomendado - Legível e manutenível
namedTemplate.query(
    """
    SELECT * FROM users 
    WHERE department = :dept 
      AND salary > :minSalary
      AND active = :active
    """,
    params,
    rowMapper
);

// ❌ Difícil de ler
jdbcTemplate.query(
    "SELECT * FROM users WHERE department = ? AND salary > ? AND active = ?",
    rowMapper,
    "IT",
    50000,
    true
);
```

### 3. Use Transações para Operações Relacionadas

```java
// ✅ Bom
jdbcTemplate.inTransaction(conn -> {
    // Múltiplas operações que devem ser atômicas
    return null;
});

// ❌ Evite
jdbcTemplate.update("INSERT...");
jdbcTemplate.update("UPDATE...");
// Se a segunda falhar, a primeira já foi commitada
```

### 4. Mapas Imutáveis para Parâmetros

```java
// ✅ Recomendado
Map<String, Object> params = Map.of(
    "id", userId,
    "name", userName
);

// ❌ Mutable - pode causar problemas
Map<String, Object> params = new HashMap<>();
params.put("id", userId);
params.put("name", userName);
```

### 5. Utilize BeanPropertyRowMapper quando Possível

```java
// ✅ Mais limpo
List<User> users = jdbcTemplate.query(
    "SELECT id, name, email FROM users",
    new BeanPropertyRowMapper<>(User.class)
);

// ❌ Verboso se a classe tem muitos campos
List<User> users = jdbcTemplate.query(
    "SELECT id, name, email, ... FROM users",
    rs -> new User(
        rs.getString("id"),
        rs.getString("name"),
        rs.getString("email"),
        // ... muitos mais
    )
);
```

### 6. Trate Optional Corretamente

```java
// ✅ Bom
User user = getUserById(userId)
    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

// ✅ Também bom
Optional<User> user = getUserById(userId);
if (user.isPresent()) {
    doSomething(user.get());
}

// ❌ Evite
User user = getUserById(userId).get(); // NPE se não existir
```

### 7. Use Prepared Statements

```java
// ✅ Seguro contra SQL Injection
jdbcTemplate.query(
    "SELECT * FROM users WHERE name = ?",
    rs -> /* ... */,
    userInput // Será escapado automaticamente
);

// ❌ Nunca faça isso
String sql = "SELECT * FROM users WHERE name = '" + userInput + "'";
// Vulnerável a SQL injection!
```

### 8. Feche Recursos

O JdbcTemplate fecha automaticamente recursos (Connection, PreparedStatement, ResultSet), mas se usar conexões diretas:

```java
try (Connection conn = dataSource.getConnection();
     PreparedStatement ps = conn.prepareStatement(sql)) {
    // Use a conexão
} catch (SQLException e) {
    // Tratamento de erro
}
// Recursos são fechados automaticamente
```

---

## Resumo das Operações

| Operação | Método | Uso |
|---|---|---|
| INSERT | `insertAndReturnKey()` | Insere e retorna ID gerado |
| INSERT/UPDATE/DELETE | `update()` | Operações que modificam dados |
| SELECT (múltiplos) | `query()` | Retorna `List<T>` |
| SELECT (um) | `queryForObject()` | Retorna `Optional<T>` |
| Lote | `batchUpdate()` | Múltiplas operações em lote |
| Transação | `inTransaction()` | Executa múltiplas operações atomicamente |
| Custom | `execute()` | Controle manual de PreparedStatement |

---

## Conclusão

A API JDBC do JToolbox fornece uma abstração clara sobre JDBC puro, mantendo flexibilidade e segurança. Seguindo as boas práticas, você pode construir camadas de acesso a dados robustas e fáceis de manter.

Para mais informações, consulte:
- Classe `Jdbc` para construção de conexões
- Classe `JdbcTemplate` para operações comuns
- Classe `NamedParameterJdbcTemplate` para parâmetros nomeados
- Interface `RowMapper` para mapeamento de resultados

