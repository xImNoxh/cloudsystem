package de.polocloud.permission.api.database.pool;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RepositoryPool {

    public static final List<Object> list = new CopyOnWriteArrayList<>();

    public static <T extends SqlRepository<?, ?>> T getRepository(Class<? extends SqlRepository> repository) {
        for (Object o : list) {
            if(o.equals(repository)) return (T) o;
        }
        return null;
    }

}
