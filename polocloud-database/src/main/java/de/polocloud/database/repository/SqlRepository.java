package de.polocloud.database.repository;

import java.util.List;

public interface SqlRepository<T, ID> {

    List<T> findAll();

    T findById(ID id);

    boolean create(T t);

    boolean removeById(ID id);

    boolean save(T t);

}

