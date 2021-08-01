package de.polocloud.plugin.bootstrap;

import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.function.BootstrapFunction;
import de.polocloud.plugin.function.NetworkRegisterFunction;
import de.polocloud.plugin.listener.CollectiveProxyEvents;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.connections.NetworkLoginCache;
import de.polocloud.plugin.protocol.register.NetworkPluginRegister;
import de.polocloud.plugin.protocol.register.NetworkProxyRegister;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyBootstrap extends Plugin implements BootstrapFunction, NetworkRegisterFunction {

    private NetworkLoginCache networkLoginCache;

    @Override
    public void onEnable() {
        this.networkLoginCache = new NetworkLoginCache();
        new CloudPlugin(this, this);
    }

    @Override
    public void executeCommand(String command) {
        getProxy().getPluginManager().dispatchCommand(getProxy().getConsole(), command.substring(0, command.length() - 1));
    }

    @Override
    public int getNetworkPort() {
        return -1;
    }

    @Override
    public void callNetwork(NetworkClient networkClient) {
        new NetworkProxyRegister(networkClient, networkLoginCache, this);
        new NetworkPluginRegister(networkClient, this);
    }

    @Override
    public void registerEvents(NetworkClient networkClient) {
        new CollectiveProxyEvents(this, networkClient, networkLoginCache);
    }

    @Override
    public void shutdown() {
        ProxyServer.getInstance().stop();
    }


}
