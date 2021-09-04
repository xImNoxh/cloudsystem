package de.polocloud.plugin.bootstrap.proxy;

import de.polocloud.api.bridge.PoloPluginBridge;
import de.polocloud.api.bridge.PoloPluginBungeeBridge;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.bootstrap.IBootstrap;
import de.polocloud.plugin.bootstrap.proxy.events.CollectiveProxyEvents;
import de.polocloud.plugin.bootstrap.proxy.register.ProxyPacketRegister;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;

public class ProxyBootstrap extends Plugin implements IBootstrap {

    private int port = -2;
    private CloudPlugin cloudPlugin;
    private PoloPluginBridge bridge;

    @Override
    public synchronized void onLoad() {
        this.bridge = new PoloPluginBungeeBridge() {

            @Override
            public boolean isPlayerOnline(UUID uniqueId) {
                return ProxyServer.getInstance().getPlayer(uniqueId) != null;
            }

            @Override
            public void connect(UUID uniqueId, String server) {

                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                if (player == null) {
                    return ;
                }
                ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
                if (serverInfo == null) {
                    return;
                }
                player.connect(serverInfo);
            }

            @Override
            public boolean hasPermission(UUID uniqueId, String permission) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                if (player == null) {
                    return false;
                }
                return player.hasPermission(permission);
            }

            @Override
            public void sendMessage(UUID uniqueId, String message) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                if (player == null) {
                    return;
                }
                player.sendMessage(message);
            }

            @Override
            public void sendTabList(UUID uniqueId, String header, String footer) {

                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                if (player == null) {
                    return;
                }
                player.setTabHeader(TextComponent.fromLegacyText(header), TextComponent.fromLegacyText(footer));
            }

            @Override
            public void broadcast(String message) {
                ProxyServer.getInstance().broadcast(message);
            }

            @Override
            public void kickPlayer(UUID uniqueId, String reason) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                if (player == null) {
                    return;
                }
                player.disconnect(reason);
            }

            @Override
            public void sendTitle(UUID uniqueId, String title, String subTitle) {

                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                if (player == null) {
                    return;
                }

                Title t = ProxyServer.getInstance().createTitle();

                t.title(TextComponent.fromLegacyText(title));
                t.subTitle(TextComponent.fromLegacyText(subTitle));

                t.send(player);
            }

            @Override
            public void sendActionbar(UUID uniqueId, String message) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                if (player == null) {
                    return;
                }
                player.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            }

            @Override
            public void executeCommand(String command) {
                getProxy().getPluginManager().dispatchCommand(getProxy().getConsole(), command.substring(0, command.length() - 1));
            }

            @Override
            public PoloType getEnvironment() {
                return PoloType.PLUGIN_PROXY;
            }

            @Override
            public void shutdown() {
                ProxyServer.getInstance().stop();
            }
        };
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
    public PoloPluginBridge getBridge() {
        return bridge;
    }

    @Override
    public int getPort() {
        JsonData jsonData = this.cloudPlugin.getJson();
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
    public void registerListeners() {
        new CollectiveProxyEvents(this);
    }

    @Override
    public void registerPacketListening() {
        new ProxyPacketRegister(this);
    }
}
