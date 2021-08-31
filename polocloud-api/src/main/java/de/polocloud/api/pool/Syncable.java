package de.polocloud.api.pool;

import java.io.Serializable;

public interface Syncable<V> extends Serializable {

    /**
     * Updates the current object
     * from cache and returns same type object
     *
     * @return object from cache
     */
    V sync();

}
