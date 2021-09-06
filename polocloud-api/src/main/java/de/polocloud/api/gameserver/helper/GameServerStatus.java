package de.polocloud.api.gameserver.helper;

import java.io.Serializable;

public enum GameServerStatus implements Serializable {

    /**
     * The server is being started
     */
    STARTING,

    /**
     * The server should not be seen
     */
    INVISIBLE,

    /**
     * The server is available to connect to
     */
    AVAILABLE,

    /**
     * The server is stopping
     */
    STOPPING

}
