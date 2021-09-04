package de.polocloud.plugin.bootstrap.proxy.register;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.packets.api.EventPacket;
import de.polocloud.api.network.packets.api.other.GlobalCachePacket;
import de.polocloud.api.network.packets.gameserver.GameServerUnregisterPacket;
import de.polocloud.api.network.packets.gameserver.permissions.PermissionCheckResponsePacket;
import de.polocloud.api.network.packets.gameserver.proxy.ProxyTablistUpdatePacket;
import de.polocloud.api.network.packets.master.MasterPlayerRequestJoinResponsePacket;
import de.polocloud.api.network.packets.master.MasterPlayerSendMessagePacket;
import de.polocloud.api.network.packets.master.MasterPlayerSendToServerPacket;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.property.GameServerProperty;
import de.polocloud.plugin.protocol.register.SimplePacketRegister;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

public class ProxyPacketRegister {

    public ProxyPacketRegister(Plugin plugin) {

        NetworkClient networkClient = CloudPlugin.getCloudPluginInstance().getNetworkClient();
        GameServerProperty property = CloudPlugin.getCloudPluginInstance().getGameServerProperty();

        new SimplePacketRegister<MasterPlayerRequestJoinResponsePacket>(MasterPlayerRequestJoinResponsePacket.class, packet -> {
            LoginEvent loginEvent = property.getGameServerLoginEvents().remove(packet.getUuid());
            if (packet.getSnowflake() == -1) {
                loginEvent.setCancelled(true);
                loginEvent.setCancelReason(new TextComponent("§cNo fallback server found!"));
            } else
                property.getGameServerLoginServers().put(loginEvent.getConnection().getUniqueId(), packet.getServiceName());
            loginEvent.completeIntent(plugin);
        });

        new SimplePacketRegister<>(GlobalCachePacket.class, (Consumer<GlobalCachePacket>) globalCachePacket -> {

            for (IGameServer gameServer : globalCachePacket.getMasterCache().getGameServers()) {
                if (gameServer.getTemplate().getTemplateType() == TemplateType.PROXY) {
                    continue;
                }
                ProxyServer.getInstance().getServers().put(gameServer.getName(), ProxyServer.getInstance().constructServerInfo(gameServer.getName(), new InetSocketAddress(gameServer.getHost(), gameServer.getPort()), "PoloCloud", false));
            }
        });

        new SimplePacketRegister<MasterPlayerSendToServerPacket>(MasterPlayerSendToServerPacket.class, packet -> {
            UUID uuid = packet.getUuid();
            if (ProxyServer.getInstance().getPlayer(uuid) != null) {
                ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(packet.getTargetServer());
                if (serverInfo != null) ProxyServer.getInstance().getPlayer(uuid).connect(serverInfo);
            }
        });

        new SimplePacketRegister<EventPacket>(EventPacket.class, eventPacket -> {

            Runnable runnable = () -> {
                if (Arrays.asList(eventPacket.getIgnoredTypes()).contains(PoloCloudAPI.getInstance().getType())) {
                    return;
                }

                if(eventPacket.getExcept().equalsIgnoreCase("null")){
                    return;
                }
                PoloCloudAPI.getInstance().getEventManager().fireEventLocally(eventPacket.getEvent());
            };

            if (eventPacket.isAsync()) {
                Scheduler.runtimeScheduler().async().schedule(runnable);
            } else {
                runnable.run();
            }
        });

        new SimplePacketRegister<MasterPlayerSendMessagePacket>(MasterPlayerSendMessagePacket.class, packet -> {
            UUID uuid = packet.getUuid();
            if (ProxyServer.getInstance().getPlayer(uuid) != null)
                ProxyServer.getInstance().getPlayer(uuid).sendMessage(TextComponent.fromLegacyText(packet.getMessage()));
        });

        new SimplePacketRegister<GameServerUnregisterPacket>(GameServerUnregisterPacket.class, packet -> ProxyServer.getInstance().getServers().remove(packet.getName()));

        new SimplePacketRegister<ProxyTablistUpdatePacket>(ProxyTablistUpdatePacket.class, packet -> {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getUuid());
            if (player != null)
                player.setTabHeader(new TextComponent(packet.getHeader()), new TextComponent(packet.getFooter()));
        });

        new SimplePacketRegister<PermissionCheckResponsePacket>(PermissionCheckResponsePacket.class, packet -> {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getPlayer());
            if (player != null) packet.setResponse(player.hasPermission(packet.getPermission()));
            networkClient.sendPacket(packet);
        });

    }
}
