package com.github.rickmvi.jtoolbox.jdbc.dao;

import com.github.rickmvi.jtoolbox.util.Try;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class JdbcTemplate {

    private final DataSource dataSource;

    public JdbcTemplate(DataSource dataSource) {
        if (dataSource == null) throw new IllegalArgumentException("dataSource must not be null");
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public int update(String sql, Object... params) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to execute update", e);
        }
    }

    public long insertAndReturnKey(String sql, Object... params) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(ps, params);
            int affected = ps.executeUpdate();
            if (affected == 0) throw new DataAccessException("Insert did not affect any rows");

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                } else {
                    throw new DataAccessException("No generated key returned");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to execute insertAndReturnKey", e);
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(mapper.mapRow(rs));
                }
                return results;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to execute query", e);
        }
    }

    public <T> Optional<T> queryForObject(String sql, RowMapper<T> mapper, Object... params) {
        List<T> list = query(sql, mapper, params);
        if (list.isEmpty()) return Optional.empty();
        if (list.size() > 1) throw new DataAccessException("queryForObject returned more than one row");
        return Optional.ofNullable(list.getFirst());
    }


    public <T> T inTransaction(Function<Connection, T> action) {
        try (Connection conn = getConnection()) {
            try {
                boolean previousAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);
                try {
                    T result = action.apply(conn);
                    conn.commit();
                    return result;
                } catch (Exception e) {
                    Try.runThrowing(conn::rollback).onFailure(ex -> {});
                    throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
                } finally {
                    conn.setAutoCommit(previousAutoCommit);
                }
            } catch (SQLException e) {
                throw new DataAccessException("Failed to execute transaction", e);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to obtain connection for transaction", e);
        }
    }

    public void execute(String sql, PreparedStatementSetter setter) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.setValues(ps);
            ps.execute();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to execute statement", e);
        }
    }

    public int[] batchUpdate(String sql, List<Object[]> batchParams) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] params : batchParams) {
                setParameters(ps, params);
                ps.addBatch();
            }
            return ps.executeBatch();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to execute batchUpdate", e);
        }
    }

    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        if (params == null) return;
        for (int i = 0; i < params.length; i++) {
            Object p = params[i];
            switch (p) {
                case null -> ps.setObject(i + 1, null);
                case Integer integer -> ps.setInt(i + 1, integer);
                case Long l -> ps.setLong(i + 1, l);
                case String s -> ps.setString(i + 1, s);
                case Boolean b -> ps.setBoolean(i + 1, b);
                case Double v -> ps.setDouble(i + 1, v);
                case Float v -> ps.setFloat(i + 1, v);
                case Date date -> ps.setDate(i + 1, date);
                case Timestamp timestamp -> ps.setTimestamp(i + 1, timestamp);
                default -> ps.setObject(i + 1, p);
            }
        }
    }

}
