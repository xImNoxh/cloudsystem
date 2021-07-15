package de.polocloud.plugin.function;

import de.polocloud.plugin.protocol.NetworkClient;

public interface BootstrapFunction {

    void executeCommand(String command);

    int getNetworkPort();

    void registerEvents(NetworkClient networkClient);

    void shutdown();

    void initStatisticChannel(NetworkClient networkClient);

    int getMaxPlayers();

}
