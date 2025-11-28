package com.github.rickmvi.jtoolbox.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class NamedParameterJdbcTemplate {

    private final JdbcTemplate delegate;

    private static final char PARAMETER_PREFIX = ':';
    private static final char PARAMETER_NAME_UNDERSCORE = '_';
    private static final char POSITIONAL_PARAMETER_PLACEHOLDER = '?';

    public NamedParameterJdbcTemplate(JdbcTemplate delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    public int update(String sql, Map<String, ?> params) {
        ParsedSql ps = parseSql(sql);
        Object[] values = buildValues(ps, params);
        return delegate.update(ps.sql, values);
    }

    public <T> java.util.List<T> query(String sql, Map<String, ?> params, RowMapper<T> mapper) {
        ParsedSql ps = parseSql(sql);
        Object[] values = buildValues(ps, params);
        return delegate.query(ps.sql, mapper, values);
    }

    public <T> Optional<T> queryForObject(String sql, Map<String, ?> params, RowMapper<T> mapper) {
        ParsedSql ps = parseSql(sql);
        Object[] values = buildValues(ps, params);
        return delegate.queryForObject(ps.sql, mapper, values);
    }

    private record ParsedSql(String sql, List<String> names) {
    }

    private ParsedSql parseSql(String namedParameterSql) {
        StringBuilder parsedSql = new StringBuilder();
        List<String> parameterNames = new ArrayList<>();
        char[] sqlChars = namedParameterSql.toCharArray();
        int currentIndex = 0;

        while (currentIndex < sqlChars.length) {
            char currentChar = sqlChars[currentIndex];
            if (currentChar == PARAMETER_PREFIX) {
                int nextIndex = currentIndex + 1;
                String parameterName = parseParameterName(sqlChars, nextIndex);
                if (parameterName.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Parameter name expected at position " + currentIndex + " in: " + namedParameterSql
                    );
                }
                parameterNames.add(parameterName);
                parsedSql.append(POSITIONAL_PARAMETER_PLACEHOLDER);
                currentIndex = nextIndex + parameterName.length();
            } else {
                parsedSql.append(currentChar);
                currentIndex++;
            }
        }

        return new ParsedSql(parsedSql.toString(), parameterNames);
    }

    private String parseParameterName(char[] sqlChars, int startIndex) {
        StringBuilder nameBuilder = new StringBuilder();
        int index = startIndex;

        while (index < sqlChars.length) {
            char candidateChar = sqlChars[index];
            if (Character.isLetterOrDigit(candidateChar) || candidateChar == PARAMETER_NAME_UNDERSCORE) {
                nameBuilder.append(candidateChar);
                index++;
            } else {
                break;
            }
        }

        return nameBuilder.toString();
    }


    private Object[] buildValues(ParsedSql parsed, Map<String, ?> params) {
        if (parsed.names.isEmpty()) return new Object[0];
        Object[] values = new Object[parsed.names.size()];
        for (int i = 0; i < parsed.names.size(); i++) {
            String name = parsed.names.get(i);
            if (!params.containsKey(name)) {
                throw new IllegalArgumentException("Missing parameter: " + name);
            }
            values[i] = params.get(name);
        }
        return values;
    }

}

