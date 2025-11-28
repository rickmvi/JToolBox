package com.github.rickmvi.jtoolbox.jdbc;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public final class Jdbc {

    private Jdbc() {
    }

    public static Optional<Builder> url(String typeOrRaw) {
        return Builder.of(typeOrRaw);
    }

    public static Builder url(DatabaseType type) {
        return Builder.of(type);
    }

    public static final class Builder {
        private final DatabaseType type;
        private final String rawUrl;
        private final String host;
        private final Integer port;
        private final String database;
        private final String username;
        private final String password;
        private final String driverClass;

        private Builder(
                DatabaseType type,
                String rawUrl,
                String host,
                Integer port,
                String database,
                String username,
                String password,
                String driverClass
        ) {
            this.type = type;
            this.rawUrl = rawUrl;
            this.host = host;
            this.port = port;
            this.database = database;
            this.username = username;
            this.password = password;
            this.driverClass = driverClass;
        }

        static Optional<Builder> of(String typeOrRaw) {
            if (typeOrRaw == null) return Optional.empty();
            try {
                DatabaseType t = DatabaseType.valueOf(typeOrRaw.toUpperCase());
                return Optional.of(new Builder(t, null, null, null, null, null, null, null));
            } catch (IllegalArgumentException e) {
                return Optional.of(new Builder(null, typeOrRaw, null, null, null, null, null, null));
            }
        }

        @Contract(value = "_ -> new", pure = true)
        static @NotNull Builder of(DatabaseType type) {
            return new Builder(type, null, null, null, null, null, null, null);
        }

        @Contract(value = "_ -> new", pure = true)
        public @NotNull Builder host(String host) {
            return new Builder(this.type, this.rawUrl, host, this.port, this.database, this.username, this.password, this.driverClass);
        }

        @Contract(value = "_ -> new", pure = true)
        public @NotNull Builder port(int port) {
            return new Builder(this.type, this.rawUrl, this.host, port, this.database, this.username, this.password, this.driverClass);
        }

        @Contract(value = "_ -> new", pure = true)
        public @NotNull Builder database(String database) {
            return new Builder(this.type, this.rawUrl, this.host, this.port, database, this.username, this.password, this.driverClass);
        }

        @Contract(value = "_ -> new", pure = true)
        public @NotNull Builder user(String username) {
            return new Builder(this.type, this.rawUrl, this.host, this.port, this.database, username, this.password, this.driverClass);
        }

        @Contract(value = "_ -> new", pure = true)
        public @NotNull Builder password(String password) {
            return new Builder(this.type, this.rawUrl, this.host, this.port, this.database, this.username, password, this.driverClass);
        }

        @Contract(value = "_ -> new", pure = true)
        public @NotNull Builder driverClass(String driverClass) {
            return new Builder(this.type, this.rawUrl, this.host, this.port, this.database, this.username, this.password, driverClass);
        }

        public String buildUrl() {
            if (rawUrl != null) return rawUrl;
            if (type == null) throw new IllegalStateException("No DatabaseType or raw URL configured");
            URLBuilder builder = URLBuilder.instance(type);
            if (host != null) builder.host(host);
            if (port != null) builder.port(port);
            if (database != null) builder.database(database);
            return builder.build();
        }

        private String resolveDriver() {
            if (this.driverClass != null) return this.driverClass;

            if (type != null) return driverFor(type);

            if (rawUrl != null) {
                try {
                    DatabaseType t = DatabaseType.fromPrefix(rawUrl);
                    return driverFor(t);
                } catch (IllegalArgumentException ignored) {
                    return null;
                }
            }
            return null;
        }

        @Contract(pure = true)
        private static @NotNull String driverFor(DatabaseType type) {
            return switch (type) {
                case POSTGRESQL -> "org.postgresql.Driver";
                case MYSQL -> "com.mysql.cj.jdbc.Driver";
                case SQLITE -> "org.sqlite.JDBC";
                case ORACLE -> "oracle.jdbc.OracleDriver";
                case SQLSERVER -> "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                case H2 -> "org.h2.Driver";
            };
        }

        public Connection connect() {
            String url = buildUrl();
            String driver = resolveDriver();
            if (driver != null) LoadDriver.load(driver);
            try {
                if (username == null || password == null) {
                    return DriverManager.getConnection(url);
                }
                return DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to connect to " + url, e);
            }
        }

        public DataSource toDataSource() {
            String url = buildUrl();
            String driver = resolveDriver();
            if (driver != null) LoadDriver.load(driver);
            final String u = url;
            final String user = username;
            final String pass = password;
            return new DataSource() {
                @Override
                public Connection getConnection() throws SQLException {
                    if (user == null || pass == null) return DriverManager.getConnection(u);
                    return DriverManager.getConnection(u, user, pass);
                }

                @Override
                public Connection getConnection(String username, String password) throws SQLException {
                    return DriverManager.getConnection(u, username, password);
                }

                @Override
                public <T> T unwrap(Class<T> iface) { throw new UnsupportedOperationException(); }

                @Override
                public boolean isWrapperFor(Class<?> iface) { return false; }

                @Override
                public java.io.PrintWriter getLogWriter() { throw new UnsupportedOperationException(); }

                @Override
                public void setLogWriter(java.io.PrintWriter out) { throw new UnsupportedOperationException(); }

                @Override
                public void setLoginTimeout(int seconds) { throw new UnsupportedOperationException(); }

                @Override
                public int getLoginTimeout() { throw new UnsupportedOperationException(); }

                @Override
                public java.util.logging.Logger getParentLogger() { throw new UnsupportedOperationException(); }
            };
        }

    }

}

