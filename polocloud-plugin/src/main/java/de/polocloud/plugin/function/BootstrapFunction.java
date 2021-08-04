package de.polocloud.plugin.function;

import de.polocloud.plugin.CloudPlugin;

public interface BootstrapFunction {

    void executeCommand(String command);

    int getNetworkPort();

    void registerEvents(CloudPlugin cloudPlugin);

    void shutdown();

}
