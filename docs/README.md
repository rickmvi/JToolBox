# JToolbox — Small Java Utilities

[![License](https://img.shields.io/badge/license-LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0.html)  
[![Maven Central](https://img.shields.io/maven-central/v/io.github.rickmvi/jtoolbox.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.rickmvi/jtoolbox)

A compact collection of Java utility libraries designed to make everyday programming tasks easier and more expressive. JToolbox favors small, focused helpers and clear APIs that play well in both CLI tools and larger applications.

This repository contains multiple utility modules grouped by responsibility — console helpers, lightweight JDBC utilities, collection helpers, JSON utilities, and more.

---

### Why JToolbox?

- Pragmatic and minimal: small, dependency-light utilities that are easy to read and integrate.
- Tested building blocks: utilities designed for clarity and maintainability.
- IDE-friendly: works well in projects where you want a few reliable helpers without heavy frameworks.

**Quick facts**

- Language: Java 8+ compatible
- Build: Gradle
- Primary extras: Optional Lombok usage, small helper modules

---

**Table of contents**

- Project overview
- Key modules & features
- Quick start
- Examples
- Contributing
- License & contact

---

**Project overview**

JToolbox groups small utilities into coherent packages. The goal is not to replace big frameworks but to offer readable, well-scoped helpers you can copy or depend on from Maven Central.

Key modules & notable packages

- console — Console I/O helpers, formatting, safe Scanner utilities (e.g., `ScannerUtils`, `Out`, `Formatted`).
- jdbc — Lightweight JDBC utilities and helpers (connection builders, `JdbcTemplate`, named-parameter support, row mapping helpers).
- collections — Small collection helpers and lightweight wrappers.
- datetime — Simple date/time helpers.
- dotenv — Loading .env files and environment helpers.
- file — File utilities and watchers.
- json — Small JSON helpers (lightweight wrappers for common tasks).
- logger — Minimal logging helpers with ANSI color support.
- http — Basic HTTP server/route and handler utilities.

Each package aims for single-responsibility, simple APIs and sensible defaults.

Highlights and roadmap (selected)

- Improved JDBC API: a builder-style `Jdbc` connector, `JdbcTemplate` and `NamedParameterJdbcTemplate` with optional `DataSource` support (for connection pools) and `BeanPropertyRowMapper` for simple bean mapping.
- Clean-code and SOLID-friendly designs across helpers. Future improvements will continue reducing duplication and increasing test coverage.
- Support for in-memory H2 database for tests and small demos.

---

**Quick start**

Add the library from Maven Central (example with Gradle Kotlin DSL):

```gradle
dependencies {
    implementation("io.github.rickmvi:jtoolbox:1.10.52")
}
```

If you prefer to import project sources directly, include the module in your multi-module build or copy the packages you need into your project.

**Examples**

Console formatting

```java
import com.github.rickmvi.formatted.Formatted;

String out = Formatted.format("Hello {}, you are {} years old", "Alice", 30);
System.out.println(out);
```

Safe console input

```java
import com.github.rickmvi.console.Scan;

int age = Scan.readInt();
Scan.close();
```

JDBC (builder-style connection)

```java
import com.github.rickmvi.jtoolbox.jdbc.Jdbc;

// Example builder-style connection — consult the jdbc package API for exact names
var conn = Jdbc.url("mysql")
    .host("localhost")
    .port(3306)
    .database("mydb")
    .user("root")
    .password("secret")
    .connect();

// Use try-with-resources for safety
try (var c = conn) {
    // plain JDBC or use JdbcTemplate helpers from jdbc package
}
```

Named parameters and row mapping (concept)

- `NamedParameterJdbcTemplate` allows executing SQL with named parameters instead of positional `?` placeholders.
- `BeanPropertyRowMapper` maps ResultSet rows to simple Java beans using property naming conventions.

See the `com.github.rickmvi.jtoolbox.jdbc` package for usage examples and API details.

**Contributing**

Contributions are welcome. Recommended workflow:

1. Open an issue describing the change or bug.
2. Fork the repository and create a feature branch.
3. Add tests for new features where appropriate.
4. Open a pull request with a clear description and targeted changes.

**Guidelines**

- Keep changes small and focused.
- Prefer readable code and tests that explain the expected behavior.
- Document public API additions in Javadoc.

**License**

JToolbox is licensed under the GNU Lesser General Public License v3.0 (LGPL-3.0). See the full license for details: https://www.gnu.org/licenses/lgpl-3.0.html

**Contact**

Author: *Rick M. Viana*

Email: *rickmviana.dev@outlook.com*

---
