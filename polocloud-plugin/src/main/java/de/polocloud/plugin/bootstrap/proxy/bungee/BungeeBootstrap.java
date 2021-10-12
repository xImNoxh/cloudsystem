package de.polocloud.plugin.bootstrap.proxy.bungee;

import de.polocloud.api.bridge.PoloPluginBridge;
import de.polocloud.api.bridge.PoloPluginBungeeBridge;
import de.polocloud.api.chat.ClickAction;
import de.polocloud.api.chat.CloudComponent;
import de.polocloud.api.chat.HoverAction;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.logger.def.Pair;
import de.polocloud.api.player.extras.IPlayerSettings;
import de.polocloud.api.player.def.SimplePlayerSettings;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.bootstrap.IBootstrap;
import de.polocloud.plugin.bootstrap.proxy.bungee.events.CollectiveProxyEvents;
import de.polocloud.plugin.bootstrap.proxy.bungee.register.ProxyPacketRegister;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;

public class BungeeBootstrap extends Plugin implements IBootstrap {

    private int port = -2;
    private CloudPlugin cloudPlugin;
    private PoloPluginBridge bridge;

    @Override
    public synchronized void onLoad() {
        this.bridge = new PoloPluginBungeeBridge() {

            @Override
            public long getPing(UUID uniqueId) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                if (player == null) {
                    return -1;
                }
                return player.getPing();
            }

            @Override
            public IPlayerSettings getSettings(UUID uniqueId) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);

                if (player == null) {
                    return null;
                }

                return new SimplePlayerSettings(
                    player.getLocale(),
                    player.hasChatColors(),
                    player.getViewDistance(),
                    player.getSkinParts().hasHat(),
                    player.getSkinParts().hasJacket(),
                    player.getSkinParts().hasRightSleeve(),
                    player.getSkinParts().hasLeftSleeve(),
                    player.getSkinParts().hasRightPants(),
                    player.getSkinParts().hasLeftPants(),
                    player.getSkinParts().hasCape(),
                    IPlayerSettings.ChatMode.valueOf(player.getChatMode().name()),
                    IPlayerSettings.MainHand.valueOf(player.getMainHand().name())
                );
            }

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
            public void sendComponent(UUID uuid, CloudComponent component) {

                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
                if (player == null) {
                    return;
                }
                player.sendMessage(createTextComponentFromCloudRecursive(component));
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
                player.sendMessage(TextComponent.fromLegacyText(message));
            }

            @Override
            public void sendTabList(UUID uniqueId, String header, String footer) {

                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                if (player == null) {
                    return;
                }
                player.resetTabHeader();
                player.setTabHeader(TextComponent.fromLegacyText(header), TextComponent.fromLegacyText(footer));
            }

            @Override
            public void broadcast(String message) {
                ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(message));
            }

            @Override
            public void kickPlayer(UUID uniqueId, String reason) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                if (player == null) {
                    return;
                }
                player.disconnect(TextComponent.fromLegacyText(reason));
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
        this.cloudPlugin.connectToCloud();
    }

    @Override
    public synchronized void onDisable() {

    }


    /**
     * Creates a {@link TextComponent} from a {@link CloudComponent}
     *
     * @param chatComponent the cloudComponent
     * @return built md5 textComponent
     */
    private TextComponent createTextComponentFromCloudRecursive(CloudComponent chatComponent) {
        TextComponent textComponent = new TextComponent(chatComponent.getMessage());
        for (Pair<ClickAction, String> clickEvent : chatComponent.getClickEvents()) {
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(clickEvent.getKey().name()), clickEvent.getValue()));
        }

        for (Pair<HoverAction, String> hoverEvent : chatComponent.getHoverEvents()) {
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(hoverEvent.getKey().name()), new BaseComponent[]{new TextComponent(hoverEvent.getValue())}));
        }

        for (CloudComponent cloudComponent : chatComponent.getSubComponents()) {
            textComponent.addExtra(createTextComponentFromCloudRecursive(cloudComponent));
        }
        return textComponent;
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
