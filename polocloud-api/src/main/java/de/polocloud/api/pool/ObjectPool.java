package de.polocloud.api.pool;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ObjectPool<V extends PoloObject<V>> extends Iterable<V> {

    /**
     * Loads a list of all cached objects
     *
     * @return list
     */
    List<V> getAllCached();

    /**
     * Loads a list of all cached objects
     * if they match a given condition
     *
     * @param filter the condition
     * @return list
     */
    default List<V> getAllCached(Predicate<V> filter) {
        return this.getAllCached().stream().filter(filter).collect(Collectors.toList());
    }

    /**
     * Updates an Object within the cache
     *
     * @param object the object to update
     */
    default void updateObject(V object) {
        if (object == null) {
            return;
        }
        V cachedObject = this.getCached(object.getName());
        if (cachedObject != null) {
            int index = this.getAllCached().indexOf(cachedObject);
            getAllCached().set(index, object);
        } else {
            getAllCached().add(object);
        }
    }

    /**
     * Sets the current cached objects
     *
     * @param cachedObjects the objects
     */
    void setCached(List<V> cachedObjects);

    /**
     * Searches for an object by its name
     *
     * @param name the name
     * @return object or null
     */
    default V getCached(String name) {
        return getOptional(name).orElse(null);
    }

    /**
     * Searches for an object by its snowflake
     *
     * @param snowflake the uuid
     * @return object or null
     */
    default V getCached(long snowflake) {
        return getOptional(snowflake).orElse(null);
    }

    /**
     * Loads an {@link Optional} for the object
     *
     * @param name the name of object
     * @return optional
     */
    default Optional<V> getOptional(String name) {
        return this.getAllCached().stream().filter(v -> v.getName().equalsIgnoreCase(name)).findFirst();
    }

    /**
     * Loads an {@link Optional} for the object
     *
     * @param snowflake the snowflake of object
     * @return optional
     */
    default Optional<V> getOptional(long snowflake) {
        return this.getAllCached().stream().filter(v -> v.getSnowflake() == snowflake).findFirst();
    }

    default Stream<V> stream() {
        return getAllCached().stream();
    }

    default void untilEmpty(Consumer<V> consumer, Runnable finishTask) {
        int count = this.getAllCached().size();
        if (count <= 0) {
            finishTask.run();
        }
        for (V v : this) {
            consumer.accept(v);
            if (count-- <= 0) {
                finishTask.run();
            }
        }
    }

    @NotNull
    @Override
    default Iterator<V> iterator() {
        return getAllCached().iterator();
    }
}
