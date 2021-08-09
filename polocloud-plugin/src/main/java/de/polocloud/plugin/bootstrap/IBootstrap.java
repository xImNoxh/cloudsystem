package de.polocloud.plugin.bootstrap;

import java.util.UUID;

public interface IBootstrap {

    void shutdown();

    int getPort();

    void registerListeners();

    void kick(UUID uuid, String message);

    void executeCommand(String command);

}
