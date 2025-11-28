package com.github.rickmvi.jtoolbox.jdbc.dao;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * Mapper simples que tenta criar instância do tipo T usando um construtor padrão ou
 * cascatear through reflection pelos campos com nomes iguais às colunas (case-insensitive).
 * Implementação leve: se não for possível mapear, lança RuntimeException.
 */
public class BeanPropertyRowMapper<T> implements RowMapper<T> {

    private final Class<T> type;

    public BeanPropertyRowMapper(Class<T> type) {
        this.type = type;
    }

    @Override
    public T mapRow(ResultSet rs) throws SQLException {
        try {

            try {
                Constructor<T> c = type.getDeclaredConstructor(ResultSet.class);
                c.setAccessible(true);
                return c.newInstance(rs);
            } catch (NoSuchMethodException ignored) {
            }

            Constructor<T> c0 = type.getDeclaredConstructor();
            c0.setAccessible(true);
            T instance = c0.newInstance();

            for (Field f : type.getDeclaredFields()) {
                String name = f.getName().toLowerCase(Locale.ROOT);
                try {
                    Object value = rs.getObject(name);
                    if (value != null) {
                        f.setAccessible(true);
                        f.set(instance, value);
                    }
                } catch (SQLException ignored) {
                }
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map row to " + type.getName(), e);
        }
    }
}

