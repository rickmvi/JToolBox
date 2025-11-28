package com.github.rickmvi.jtoolbox.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementSetter {

    void setValues(PreparedStatement ps) throws SQLException;

}

