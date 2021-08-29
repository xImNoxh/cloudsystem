package de.polocloud.api.gameserver.helper;

import java.io.Serializable;

public enum GameServerStatus implements Serializable {

    PENDING,
    STARTING,
    RUNNING,
    STOPPING;

}
