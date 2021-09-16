package de.polocloud.api.pool;

import de.polocloud.api.common.INamable;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.guice.own.Guice;
import de.polocloud.api.util.gson.PoloHelper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.function.Consumer;

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
