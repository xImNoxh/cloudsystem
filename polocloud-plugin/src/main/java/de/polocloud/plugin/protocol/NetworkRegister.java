package de.polocloud.plugin.protocol;

import de.polocloud.plugin.protocol.register.Register;

public abstract class NetworkRegister extends Register {

    private NetworkClient networkClient;

    public NetworkRegister(NetworkClient networkClient) {
        super(networkClient);
        this.networkClient = networkClient;
    }

    public NetworkClient getNetworkClient() {
        return networkClient;
    }

}
