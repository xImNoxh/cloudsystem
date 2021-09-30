package de.polocloud.api.pool;

import de.polocloud.api.common.INamable;
import de.polocloud.api.common.ISnowflakeable;

import java.io.Serializable;

public interface PoloObject<V> extends Serializable, ISnowflakeable, INamable {

    /**
     * Syncs the current object with the cache
     *
     * @return new object
     */
    V sync();

}
