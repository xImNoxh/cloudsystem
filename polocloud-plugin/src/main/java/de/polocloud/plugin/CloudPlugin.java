package de.polocloud.plugin;

import de.polocloud.plugin.function.BootstrapFunction;
import de.polocloud.plugin.function.NetworkRegisterFunction;
import de.polocloud.plugin.protocol.NetworkClient;

public class CloudPlugin {

    private BootstrapFunction bootstrapFunction;
    private NetworkClient networkClient;

    public CloudPlugin(BootstrapFunction bootstrapFunction, NetworkRegisterFunction networkRegisterFunction) {

        this.bootstrapFunction = bootstrapFunction;

        this.networkClient = new NetworkClient();
        this.networkClient.connect(bootstrapFunction.getNetworkPort());

        bootstrapFunction.registerEvents(networkClient);
        networkRegisterFunction.callNetwork(networkClient);
    }

    public BootstrapFunction getBootstrapFunction() {
        return bootstrapFunction;
    }
}
