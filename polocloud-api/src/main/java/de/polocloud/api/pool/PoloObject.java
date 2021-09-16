package de.polocloud.api.pool;

import de.polocloud.api.common.INamable;

import java.io.Serializable;

public interface PoloObject<V> extends Serializable, INamable {

    /**
     * The snowflake of this object
     */
    long getSnowflake();

    /**
     * Syncs the current object with the cache
     *
     * @return new object
     */
    V sync();

}
