package de.polocloud.permission.api.database.adapter;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlFunction<I, O> {

    O apply(I i) throws SQLException;
}

