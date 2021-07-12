package de.polocloud.plugin.protocol;

public abstract class NetworkRegister {

    private NetworkClient networkClient;

    public NetworkRegister(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    public NetworkClient getNetworkClient() {
        return networkClient;
    }
}
