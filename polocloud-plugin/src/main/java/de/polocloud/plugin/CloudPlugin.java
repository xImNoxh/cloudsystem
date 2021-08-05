package de.polocloud.plugin;

import de.polocloud.plugin.function.BootstrapFunction;
import de.polocloud.plugin.function.NetworkRegisterFunction;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.property.GameServerProperty;

public class CloudPlugin {

    private static CloudPlugin instance;

    private GameServerProperty property;

    private BootstrapFunction bootstrapFunction;
    private NetworkClient networkClient;

    public CloudPlugin(BootstrapFunction bootstrapFunction) {

        instance = this;

        this.bootstrapFunction = bootstrapFunction;
        this.property = new GameServerProperty();

        this.networkClient = new NetworkClient();
        this.networkClient.connect(bootstrapFunction.getNetworkPort());

    }

    public void callListeners(NetworkRegisterFunction networkRegisterFunction){
        bootstrapFunction.registerEvents(this);
        networkRegisterFunction.callNetwork(networkClient);
    }

    public GameServerProperty getProperty() {
        return property;
    }

    public static CloudPlugin getInstance() {
        return instance;
    }

    public NetworkClient getNetworkClient() {
        return networkClient;
    }

    public BootstrapFunction getBootstrapFunction() {
        return bootstrapFunction;
    }
}
