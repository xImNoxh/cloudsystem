package de.polocloud.database.functions;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlFunction<I, O> {

    O apply(I i) throws SQLException;
}


