package com.github.rickmvi.jtoolbox.jdbc;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class URLBuilder {

    private final DatabaseType DATABASE_TYPE;
    private final StringBuilder URL_BUILDER = new StringBuilder();
    private static final Map<DatabaseType, Integer> PORTS = Map.of(
                   DatabaseType.POSTGRESQL, 5432,
                   DatabaseType.MYSQL, 3306,
                   DatabaseType.SQLSERVER, 1433,
                   DatabaseType.ORACLE, 1521
    );

    private URLBuilder(DatabaseType databaseType) {
        this.DATABASE_TYPE = databaseType;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull URLBuilder instance(DatabaseType type) {
        return new URLBuilder(type);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull URLBuilder raw(@NotNull String url) {
        URLBuilder b = new URLBuilder(DatabaseType.SQLITE);
        b.URL_BUILDER.setLength(0);
        b.URL_BUILDER.append(url);
        return b;
    }

    public URLBuilder host(String host) {
        URL_BUILDER.append(DATABASE_TYPE.prefix()).append(host);
        return this;
    }

    public URLBuilder localhost() {
        return this.host("localhost");
    }

    public URLBuilder port(int port) {
        URL_BUILDER.append(":").append(port);
        return this;
    }

    public URLBuilder database(String database) {
        URL_BUILDER.append("/").append(database);
        return this;
    }

    public String build() {
        return URL_BUILDER.toString();
    }

    public static String postgresDataBase(String database) {
        return instance(DatabaseType.POSTGRESQL)
                .localhost()
                .port(PORTS.get(DatabaseType.POSTGRESQL))
                .database(database)
                .build();
    }

    public static String mysqlDataBase(String database) {
        return instance(DatabaseType.MYSQL)
                .localhost()
                .port(PORTS.get(DatabaseType.MYSQL))
                .database(database)
                .build();
    }

    public static String sqlServerDataBase(String database) {
        return instance(DatabaseType.SQLSERVER)
                .localhost()
                .port(PORTS.get(DatabaseType.SQLSERVER))
                .database(database)
                .build();
    }

    public static String oracleDataBase(String database) {
        return instance(DatabaseType.ORACLE)
                .localhost()
                .port(PORTS.get(DatabaseType.ORACLE))
                .database(database)
                .build();
    }

}
