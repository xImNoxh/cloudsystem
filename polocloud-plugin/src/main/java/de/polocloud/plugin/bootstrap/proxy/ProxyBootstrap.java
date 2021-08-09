package de.polocloud.plugin.bootstrap.proxy;

import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.bootstrap.IBootstrap;
import de.polocloud.plugin.bootstrap.proxy.events.CollectiveProxyEvents;
import de.polocloud.plugin.bootstrap.proxy.register.ProxyPacketRegister;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;

public class ProxyBootstrap extends Plugin implements IBootstrap {

    private CloudPlugin cloudPlugin;

    @Override
    public void onLoad() {
        System.out.println("Proxy plugin on load");

        this.cloudPlugin = new CloudPlugin(this);

        System.out.println("Proxy plugin end load");
    }

    @Override
    public void onEnable() {
        System.out.println("Proxy plugin on enable");

        this.cloudPlugin.onEnable();

        System.out.println("Proxy plugin end enable");
    }

    @Override
    public void onDisable() {
        System.out.println("Proxy plugin on disable");
    }

    @Override
    public void shutdown() {
        ProxyServer.getInstance().stop();
    }

    @Override
    public int getPort() {
        return -1;
    }

    @Override
    public void kick(UUID uuid, String message) {
        getProxy().getPlayer(uuid).disconnect(new TextComponent(message));
    }

    @Override
    public void executeCommand(String command) {
        getProxy().getPluginManager().dispatchCommand(getProxy().getConsole(), command.substring(0, command.length() - 1));
    }

    @Override
    public void registerListeners() {
        new CollectiveProxyEvents(this);
        new ProxyPacketRegister(this);
    }
}
