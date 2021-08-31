package de.polocloud.api.pool;

import de.polocloud.api.network.request.base.future.PoloFuture;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
     * Gets an object synced from the cloud
     * via packet and response
     * (This might take some time to process)
     *
     * @param name the name
     * @return response or null if timed out
     */
    PoloFuture<V> get(String name);

    /**
     * Gets an object synced from the cloud
     * via packet and response
     * (This might take some time to process)
     *
     * @param snowflake the snowflake
     * @return response or null if timed out
     */
    PoloFuture<V> get(long snowflake);

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

    @NotNull
    @Override
    default Iterator<V> iterator() {
        return getAllCached().iterator();
    }
}
