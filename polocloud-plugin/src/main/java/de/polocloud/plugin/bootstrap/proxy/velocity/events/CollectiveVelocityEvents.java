package de.polocloud.plugin.bootstrap.proxy.velocity.events;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.proxy.server.ServerPing;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.SimpleConsoleExecutor;
import de.polocloud.api.config.master.MasterConfig;
import de.polocloud.api.event.impl.other.ProxyConstructPlayerEvent;
import de.polocloud.api.event.impl.player.CloudPlayerLackMaintenanceEvent;
import de.polocloud.api.event.impl.player.CloudPlayerSwitchServerEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.base.SimpleGameServer;
import de.polocloud.api.logger.def.Pair;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerRegisterPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUnregisterPacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.def.SimpleCloudPlayer;
import de.polocloud.api.player.def.SimplePlayerConnection;
import de.polocloud.api.player.extras.IPlayerConnection;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.helper.GameServerVersion;
import de.polocloud.api.util.MinecraftProtocol;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.bootstrap.proxy.velocity.VelocityBootstrap;
import de.polocloud.plugin.bootstrap.proxy.velocity.other.VelocityCommandExecutor;
import de.polocloud.plugin.protocol.NetworkClient;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.UUID;

public class CollectiveVelocityEvents  {

    private final NetworkClient networkClient;

    public CollectiveVelocityEvents() {

        this.networkClient = CloudPlugin.getCloudPluginInstance().getNetworkClient();
        VelocityBootstrap.getInstance().getServer().getEventManager().register(VelocityBootstrap.getInstance(), this);
    }


    @Subscribe
    public void handle(ProxyPingEvent event) {

        InboundConnection connection = event.getConnection();
        InetSocketAddress address = connection.getRemoteAddress();

        ServerPing serverPing = event.getPing();
        ServerPing.Builder builder = serverPing.asBuilder();

        MasterConfig masterConfig = PoloCloudAPI.getInstance().getMasterConfig();
        IGameServer thisService = PoloCloudAPI.getInstance().getGameServerManager().getThisService();

        int maxPlayers = thisService == null ? 0 : thisService.getMaxPlayers();
        int onlinePlayers;

        if (masterConfig != null && masterConfig.getProperties().isSyncProxyOnlinePlayers()) {
            onlinePlayers = PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached().size();
        } else {
            onlinePlayers = thisService == null ? 0 : thisService.getOnlinePlayers();
        }

        String[] maintenancePlayerInfo = thisService == null ? new String[0] : ((SimpleGameServer)thisService).getPlayerInfo();
        ServerPing.SamplePlayer[] sample = new ServerPing.SamplePlayer[maintenancePlayerInfo.length];

        for (int i = 0; i < maintenancePlayerInfo.length; i++) {
            sample[i] = new ServerPing.SamplePlayer(replace(maintenancePlayerInfo[i], thisService, address), UUID.randomUUID());
        }

        //Setting players
        builder.onlinePlayers(onlinePlayers);
        builder.maximumPlayers(maxPlayers);
        builder.samplePlayers(sample);
        //Setting motd
        if (thisService != null && thisService.getMotd() != null) {
            builder.description(Component.text(replace(thisService.getMotd(), thisService, address)));
        }

        //Setting version
        if (thisService != null && ((SimpleGameServer) thisService).getVersionString() != null && !((SimpleGameServer) thisService).getVersionString().trim().isEmpty()) {
            builder.version(new ServerPing.Version(-1, replace(((SimpleGameServer) thisService).getVersionString(), thisService, address)));
        }

        serverPing = builder.build();
        event.setPing(serverPing);
    }


    private String replace(String input, IGameServer gameServer, InetSocketAddress address) {

        ICloudPlayer cloudPlayer = PoloCloudAPI.getInstance().getCloudPlayerManager().stream().filter(cp -> {
            return cp.getConnection().getHost().equalsIgnoreCase(address.getAddress().getHostAddress()) && cp.getConnection().getPort() == address.getPort();
        }).findFirst().orElse(null);

        if (cloudPlayer == null) {
            input = input.replace("%PROXY%", gameServer.getName());
        } else {
            input = input.replace("%PROXY%", cloudPlayer.getProxyServer().getName());
        }

        input = input.replace("%NUMBER%", String.valueOf(gameServer.getId()));
        input = input.replace("%MAX_PLAYERS%", String.valueOf(gameServer.getMaxPlayers()));
        input = input.replace("%ONLINE_PLAYERS%", String.valueOf(gameServer.getOnlinePlayers()));

        return input;
    }

    @Subscribe
    public void handle(KickedFromServerEvent event) {
        ICloudPlayer cloudPlayer = PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(event.getPlayer().getUsername());
        if (cloudPlayer == null) {
            event.getPlayer().disconnect(Component.text(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix() + "§cSome internal Cache-Error occurred!"));
            return;
        }
        cloudPlayer.sendToFallback();
    }



    @Subscribe
    public void handle(PostLoginEvent event) {
        Player player = event.getPlayer();

        if (PoloCloudAPI.getInstance().getGameServerManager().getThisService().getTemplate().isMaintenance() && !player.hasPermission("*") && !player.hasPermission("cloud.maintenance")) {
            PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerLackMaintenanceEvent(PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(player.getUniqueId())), cloudPlayerLackMaintenanceEvent -> {
                if(!cloudPlayerLackMaintenanceEvent.isCancelled()){
                    event.getPlayer().disconnect(Component.text(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getGroupMaintenanceMessage()));
                }
            });
            return;
        }

        if (VelocityBootstrap.getInstance().getServer().getAllPlayers().size() - 1 >= PoloCloudAPI.getInstance().getGameServerManager().getThisService().getMaxPlayers() && !player.hasPermission("*") && !player.hasPermission("cloud.fulljoin")) {
            event.getPlayer().disconnect(Component.text(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getServiceIsFull()));
        }
    }

    @Subscribe
    public void handle(LoginEvent event) {
        Player player = event.getPlayer();
        InetSocketAddress address = player.getRemoteAddress();

        IPlayerConnection playerConnection = new SimplePlayerConnection(
            address,
            player.getUniqueId(),
            player.getUsername(),
            address.getAddress().getHostAddress(),
            address.getPort(),
            MinecraftProtocol.valueOf(player.getProtocolVersion().getProtocol()),
            player.isOnlineMode(),
            true
        );
        ProxyConstructPlayerEvent playerEvent = PoloCloudAPI.getInstance().getEventManager().fireEvent(new ProxyConstructPlayerEvent(playerConnection));

        ICloudPlayer cloudPlayer = playerEvent.getResult();

        if (cloudPlayer == null) {
            cloudPlayer = new SimpleCloudPlayer(playerConnection.getName(), playerConnection.getUniqueId(), playerConnection);
            ((SimpleCloudPlayer)cloudPlayer).setProxyServer(PoloCloudAPI.getInstance().getGameServerManager().getThisService().getName());
        }
        cloudPlayer.update();

        PoloCloudAPI.getInstance().getCloudPlayerManager().register(cloudPlayer);
        PoloCloudAPI.getInstance().sendPacket(new CloudPlayerRegisterPacket(cloudPlayer));

        event.setResult(ResultedEvent.ComponentResult.allowed());
    }


    /**
     * Checks if the version of a player matches the version of a server
     *
     * @param playerProtocolId the protocol id of the player
     * @param gameServer the server to request
     * @return pair containing boolean (if event is cancelled) and String (message)
     */
    private Pair<Boolean, String> checkVersion(int playerProtocolId, IGameServer gameServer) {
        GameServerVersion version = gameServer.getTemplate().getVersion();
        boolean cancelled = false;
        String msg = null;

        if (version.getProtocolId() != -1) {
            int serverProtocolId = version.getProtocolId();
            MinecraftProtocol serverProtocol = MinecraftProtocol.valueOf(serverProtocolId);
            MinecraftProtocol playerProtocol = MinecraftProtocol.valueOf(playerProtocolId);


            if (!Arrays.asList(serverProtocol.getVersionNames()).contains(playerProtocol.getName())) {
                msg = PoloCloudAPI.getInstance().getMasterConfig().getMessages().getWrongMinecraftVersion();

                msg = msg.replace("%server%", gameServer.getName());
                msg = msg.replace("%required_version%", serverProtocol.getNewestVersion());
                msg = msg.replace("%your_version%", playerProtocol.getNewestVersion());

                cancelled = true;
            }
        }
        return new Pair<>(cancelled, msg);
    }

    @Subscribe
    public void handle(ServerPreConnectEvent event) {
        ProxyServer server = VelocityBootstrap.getInstance().getServer();
        Player player = event.getPlayer();
        SimpleCloudPlayer cloudPlayer = (SimpleCloudPlayer) PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(event.getPlayer().getUsername());

        //Player is joining the network
        if (player.getCurrentServer().orElse(null) == null) {

            //Searching a fallback for the player
            IGameServer fallback = PoloCloudAPI.getInstance().getFallbackManager().getFallback(cloudPlayer);

            //No fallback was found
            if (fallback == null || server.getServer(fallback.getName()).orElse(null) == null) {

                player.disconnect(Component.text(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getNoFallbackServer()));
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                return;
            }

            //Sending player to fallback
            RegisteredServer serverInfo = server.getServer(fallback.getName()).orElse(null);

            if (serverInfo == null) {
                System.out.println("[CloudPlugin] Null Fallback! Please report this error!");
                return;
            }

            Pair<Boolean, String> eventData = checkVersion(player.getProtocolVersion().getProtocol(), fallback);

            if (eventData.getKey()) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                player.disconnect(Component.text(eventData.getValue()));
            } else {
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(serverInfo));
            }

            //Setting the new Server from the player
            Scheduler.runtimeScheduler().schedule(() -> {
                if (serverInfo.getServerInfo() == null) {
                    return;
                }
                cloudPlayer.setMinecraftServer(serverInfo.getServerInfo().getName());
                cloudPlayer.update();
                PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerSwitchServerEvent(cloudPlayer, cloudPlayer.getMinecraftServer(), null), serverEvent -> {
                    if (serverEvent.isCancelled()) {
                        IGameServer target = serverEvent.getTarget();
                        cloudPlayer.sendTo(target);
                    }
                });
            }, () -> PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(player.getUsername()) != null);

        } else {
            //Player is switching servers

            if (cloudPlayer == null || cloudPlayer.getMinecraftServer() == null) {
                return;
            }
            //Setting new info for the player
            ServerInfo target = event.getOriginalServer().getServerInfo();

            IGameServer targetGameServer = PoloCloudAPI.getInstance().getGameServerManager().getCached(target.getName());
            if (targetGameServer != null) {
                Pair<Boolean, String> eventData = checkVersion(player.getProtocolVersion().getProtocol(), targetGameServer);

                if (eventData.getKey()) {
                    event.setResult(ServerPreConnectEvent.ServerResult.denied());
                    player.sendMessage(Component.text(eventData.getValue()));
                }
            }
            IGameServer from = cloudPlayer.getMinecraftServer();
            cloudPlayer.setMinecraftServer(target.getName());
            cloudPlayer.update();
            PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerSwitchServerEvent(cloudPlayer, cloudPlayer.getMinecraftServer(), from), serverEvent -> {
                if (serverEvent.isCancelled()) {
                    IGameServer t = serverEvent.getTarget();
                    cloudPlayer.sendTo(t);
                }
            });
        }
    }


    @Subscribe
    public void handleCommand(CommandExecuteEvent event) {
        String command = event.getCommand();
        CommandSource source = event.getCommandSource();

        CommandExecutor executor;

        if (source instanceof Player) {
            executor = PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(((Player)source).getUniqueId());
        } else {
            executor = new VelocityCommandExecutor();
        }

        try {
            if (PoloCloudAPI.getInstance().getCommandManager().runCommand(command, executor)) {
                event.setResult(CommandExecuteEvent.CommandResult.denied());
            } else {
                event.setResult(CommandExecuteEvent.CommandResult.allowed());
            }
        } catch (IndexOutOfBoundsException e) {
            //Maybe only "/" was entered
        }
    }


    @Subscribe @SneakyThrows
    public void handle(PlayerChooseInitialServerEvent event) {
        ProxyServer velocity = VelocityBootstrap.getInstance().getServer();
        Player player = event.getPlayer();

        IGameServer fallbackService = PoloCloudAPI.getInstance().getFallbackManager().getFallback(new SimpleCloudPlayer(player.getUsername(), player.getUniqueId(), new SimplePlayerConnection(
            player.getRemoteAddress(),
            player.getUniqueId(),
            player.getUsername(),
            player.getRemoteAddress().getAddress().getHostAddress(),
            player.getRemoteAddress().getPort(),
            MinecraftProtocol.valueOf(player.getProtocolVersion().getProtocol()),
            player.isOnlineMode(),
            true)
        ));
        if (fallbackService == null) {
            player.disconnect(Component.text(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getNoFallbackServer()));
            return;
        }

        RegisteredServer fallbackRegisteredServer = velocity.getServer(fallbackService.getName()).orElse(null);

        if (fallbackRegisteredServer == null) {
            player.disconnect(Component.text(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getNoFallbackServer()));
            return;
        }

        event.setInitialServer(fallbackRegisteredServer);
    }

    @Subscribe
    public void handlePluginMessage(PluginMessageEvent event) {

        if (event.getIdentifier().getId().equals("MC|BSign") || event.getIdentifier().getId().equals("MC|BEdit")) event.setResult(PluginMessageEvent.ForwardResult.handled());
    }

    @Subscribe
    public void handle(DisconnectEvent event) {
        ICloudPlayer cloudPlayer = PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(event.getPlayer().getUsername());
        if (cloudPlayer == null) {
            IGameServer thisService = PoloCloudAPI.getInstance().getGameServerManager().getThisService();
            PoloCloudAPI.getInstance().messageCloud("§c" + thisService.getName() + " tried to unregister '§e" + event.getPlayer().getUsername() + ":" + event.getPlayer().getUniqueId() + "§c' but it's ICloudPlayer was null!");
            return;
        }
        PoloCloudAPI.getInstance().getCloudPlayerManager().unregister(cloudPlayer);
        networkClient.sendPacket(new CloudPlayerUnregisterPacket(cloudPlayer));
    }


}
