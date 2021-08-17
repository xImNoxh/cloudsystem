package de.polocloud.plugin.bootstrap;

import de.polocloud.api.common.PoloType;

import java.util.UUID;

public interface IBootstrap {

    void shutdown();

    int getPort();

    void registerListeners();

    PoloType getType();

    void kick(UUID uuid, String message);

    void executeCommand(String command);

    void registerPacketListening();

}
