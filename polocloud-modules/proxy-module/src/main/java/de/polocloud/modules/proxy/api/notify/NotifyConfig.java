package de.polocloud.modules.proxy.api.notify;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter @AllArgsConstructor @Setter
public class NotifyConfig {

    /**
     * If this system is enabled
     */
    private boolean enabled;

    /**
     * The permission to received messages
     */
    private String permission;

    /**
     * The starting message if a server is starting
     */
    private String startingMessage;

    /**
     * The started message if a server is online
     */
    private String startedMessage;

    /**
     * The stopping message if a server stops
     */
    private String stoppedMessage;

    /**
     * All the players that have disabled
     * receiving messages
     */
    private List<UUID> disabledMessages;

}
