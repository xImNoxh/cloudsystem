package de.polocloud.plugin.bootstrap.proxy;

import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.bootstrap.IBootstrap;
import de.polocloud.plugin.bootstrap.proxy.events.CollectiveProxyEvents;
import de.polocloud.plugin.bootstrap.proxy.register.ProxyPacketRegister;
import de.polocloud.plugin.protocol.config.ConfigReader;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;

public class ProxyBootstrap extends Plugin implements IBootstrap {

    private int port = -2;
    private CloudPlugin cloudPlugin;

    @Override
    public synchronized void onLoad() {
        this.cloudPlugin = new CloudPlugin(this);
    }

    @Override
    public synchronized void onEnable() {
        this.cloudPlugin.onEnable();
    }

    @Override
    public synchronized void onDisable() {

    }

    @Override
    public void shutdown() {
        ProxyServer.getInstance().stop();
    }

    @Override
    public int getPort() {
        JsonData jsonData = ConfigReader.getJson();
        if (jsonData == null) {
            System.out.println("[ProxyBootstrap] Couldn't read JsonFile!");
            return port;
        } else {
            if (this.port == -2) {
                this.port = jsonData.getInteger("port");
            }
        }
        return this.port;
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
    }

    @Override
    public PoloType getType() {
        return PoloType.PLUGIN_PROXY;
    }

    @Override
    public void registerPacketListening() {
        new ProxyPacketRegister(this);
    }
}
