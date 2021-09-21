package de.polocloud.plugin.bootstrap.proxy.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.polocloud.api.bridge.PoloPluginBridge;
import de.polocloud.api.bridge.PoloPluginBungeeBridge;
import de.polocloud.api.chat.ClickAction;
import de.polocloud.api.chat.CloudComponent;
import de.polocloud.api.chat.HoverAction;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.player.def.SimplePlayerSettings;
import de.polocloud.api.player.extras.IPlayerSettings;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.bootstrap.IBootstrap;
import de.polocloud.plugin.bootstrap.proxy.velocity.events.CollectiveVelocityEvents;
import de.polocloud.plugin.bootstrap.proxy.velocity.register.VelocityPacketRegister;
import javafx.util.Pair;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.title.Title;
import org.slf4j.Logger;

import java.util.UUID;

@Plugin(
    id = "bridge",
    name = "PoloCloud-Plugin",
    version = "1.0.0",
    description = "This is the bridge between cloud and proxy",
    authors = "Lystx",
    url = "https://polocloud.de"
) @Getter
public class VelocityBootstrap implements IBootstrap {

    private int port = -2;
    private final CloudPlugin cloudPlugin;
    private final PoloPluginBridge bridge;

    private final ProxyServer server;
    private final Logger logger;

    @Getter
    private static VelocityBootstrap instance;

    @Inject
    public VelocityBootstrap(ProxyServer server, Logger logger) {
        instance = this;
        this.server = server;
        this.logger = logger;

        this.bridge = new PoloPluginBungeeBridge() {
            @Override
            public void connect(UUID uniqueId, String service) {
                Player player = server.getPlayer(uniqueId).orElse(null);
                if (player == null) {
                    return;
                }

                RegisteredServer registeredServer = server.getServer(service).orElse(null);
                if (registeredServer == null) {
                    return;
                }

                player.createConnectionRequest(registeredServer).connect();

            }

            @Override
            public void sendComponent(UUID uuid, CloudComponent component) {
                Player player = server.getPlayer(uuid).orElse(null);
                if (player == null) {
                    return;
                }

                player.sendMessage(createComponentFromCloudRecursive(component));
            }

            @Override
            public IPlayerSettings getSettings(UUID uniqueId) {
                Player player = server.getPlayer(uniqueId).orElse(null);
                if (player == null) {
                    return null;
                }
                return new SimplePlayerSettings(
                    player.getPlayerSettings().getLocale(),
                    player.getPlayerSettings().hasChatColors(),
                    player.getPlayerSettings().getViewDistance(),
                    player.getPlayerSettings().getSkinParts().hasHat(),
                    player.getPlayerSettings().getSkinParts().hasJacket(),
                    player.getPlayerSettings().getSkinParts().hasRightSleeve(),
                    player.getPlayerSettings().getSkinParts().hasLeftSleeve(),
                    player.getPlayerSettings().getSkinParts().hasRightPants(),
                    player.getPlayerSettings().getSkinParts().hasLeftPants(),
                    player.getPlayerSettings().getSkinParts().hasCape(),
                    IPlayerSettings.ChatMode.valueOf(player.getPlayerSettings().getChatMode().name()),
                    IPlayerSettings.MainHand.valueOf(player.getPlayerSettings().getMainHand().name())
                );
            }

            @Override
            public boolean hasPermission(UUID uniqueId, String permission) {
                Player player = server.getPlayer(uniqueId).orElse(null);
                if (player == null) {
                    return false;
                }
                return player.hasPermission(permission);
            }

            @Override
            public long getPing(UUID uniqueId) {
                Player player = server.getPlayer(uniqueId).orElse(null);
                if (player == null) {
                    return -1;
                }

                return player.getPing();
            }

            @Override
            public boolean isPlayerOnline(UUID uniqueId) {
                return server.getPlayer(uniqueId).isPresent();
            }

            @Override
            public void sendMessage(UUID uniqueId, String message) {
                Player player = server.getPlayer(uniqueId).orElse(null);
                if (player == null) {
                    return;
                }

                player.sendMessage(Component.text(message));
            }

            @Override
            public void sendTabList(UUID uniqueId, String header, String footer) {
                Player player = server.getPlayer(uniqueId).orElse(null);

                if (player == null) {
                    return;
                }

                player.sendPlayerListHeaderAndFooter(Component.text(header), Component.text(footer));
            }

            @Override
            public void broadcast(String message) {
                for (Player allPlayer : server.getAllPlayers()) {
                    allPlayer.sendMessage(Component.text(message));
                }
            }

            @Override
            public void kickPlayer(UUID uniqueId, String reason) {
                Player player = server.getPlayer(uniqueId).orElse(null);
                if (player == null) {
                    return;
                }

                player.disconnect(Component.text(reason));
            }

            @Override
            public void sendTitle(UUID uniqueId, String title, String subTitle) {

                Player player = server.getPlayer(uniqueId).orElse(null);
                if (player == null) {
                    return;
                }
                player.showTitle(Title.title(Component.text(title), Component.text(subTitle)));
            }

            @Override
            public void executeCommand(String command) {
                server.getCommandManager().executeImmediatelyAsync(server.getConsoleCommandSource(), command);
            }

            @Override
            public PoloType getEnvironment() {
                return PoloType.PLUGIN_PROXY;
            }

            @Override
            public void sendActionbar(UUID uniqueId, String message) {

                Player player = server.getPlayer(uniqueId).orElse(null);
                if (player == null) {
                    return;
                }

                player.sendActionBar(Component.text(message));
            }

            @Override
            public void shutdown() {
                server.shutdown();
            }
        };
        this.cloudPlugin = new CloudPlugin(this);
        this.cloudPlugin.onEnable();
    }



    /**
     * Creates a {@link Component} from a {@link CloudComponent}
     *
     * @param chatComponent the cloudComponent
     * @return built md5 textComponent
     */
    private Component createComponentFromCloudRecursive(CloudComponent chatComponent) {
        Component textComponent = Component.text(chatComponent.getMessage());
        for (Pair<ClickAction, String> clickEvent : chatComponent.getClickEvents()) {
            textComponent.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.valueOf(clickEvent.getKey().name()), clickEvent.getValue()));
        }

        for (Pair<HoverAction, String> hoverEvent : chatComponent.getHoverEvents()) {
            switch (hoverEvent.getKey()) {
                case SHOW_TEXT:
                case SHOW_ENTITY:
                case SHOW_ITEM:
                default:
                    textComponent.hoverEvent(HoverEvent.showText(Component.text(hoverEvent.getValue())));
            }
        }

        for (CloudComponent cloudComponent : chatComponent.getSubComponents()) {
            textComponent.append(createComponentFromCloudRecursive(cloudComponent));
        }
        return textComponent;
    }
    @Override
    public PoloPluginBungeeBridge getBridge() {
        return (PoloPluginBungeeBridge) bridge;
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
        new CollectiveVelocityEvents();
    }

    @Override
    public void registerPacketListening() {
        new VelocityPacketRegister();
    }
}
