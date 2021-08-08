package de.polocloud.permission.api.database.pool;

import java.util.List;

public interface SqlRepository<T, ID> {

    /**Find every entity in Sql-Table*/
    List<T> findAll();

    /**Find entity in Sql-Table*/
    T findById(ID id);

    /**Create entity if not exists in Sql-Table*/
    boolean create(T t);

    /**Remove entity from Sql-Table*/
    boolean removeById(ID id);

    /**Update entity in Sql-Table*/
    boolean save(T t);

}
