package de.polocloud.plugin.bootstrap.proxy.register;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.bridge.PoloPluginBungeeBridge;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.packets.api.GlobalCachePacket;
import de.polocloud.api.network.packets.gameserver.GameServerUnregisterPacket;
import de.polocloud.api.network.packets.gameserver.proxy.ProxyTablistUpdatePacket;
import de.polocloud.api.network.packets.master.MasterPlayerSendMessagePacket;
import de.polocloud.api.network.packets.master.MasterPlayerSendToServerPacket;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;
import de.polocloud.api.network.protocol.packet.base.response.ResponseState;
import de.polocloud.api.network.protocol.packet.base.response.def.Response;
import de.polocloud.api.player.extras.IPlayerSettings;
import de.polocloud.api.template.helper.TemplateType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;
import java.util.UUID;

public class ProxyPacketRegister {

    public ProxyPacketRegister(Plugin plugin) {

        //Cache updating handler
        PoloCloudAPI.getInstance().registerSimplePacketHandler(GlobalCachePacket.class, globalCachePacket -> {

            for (IGameServer gameServer : globalCachePacket.getMasterCache().getGameServers()) {
                if (gameServer.getTemplate().getTemplateType() == TemplateType.PROXY) {
                    continue;
                }
                ProxyServer.getInstance().getServers().put(gameServer.getName(), ProxyServer.getInstance().constructServerInfo(gameServer.getName(), new InetSocketAddress(gameServer.getHost(), gameServer.getPort()), "PoloCloud", false));
            }
        });

        //Connecting to server handler
        PoloCloudAPI.getInstance().registerSimplePacketHandler(MasterPlayerSendToServerPacket.class, packet -> {
            UUID uuid = packet.getUuid();
            if (ProxyServer.getInstance().getPlayer(uuid) != null) {
                ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(packet.getTargetServer());
                if (serverInfo != null) ProxyServer.getInstance().getPlayer(uuid).connect(serverInfo);
            }
        });

        //Messaging handler
        PoloCloudAPI.getInstance().registerSimplePacketHandler(MasterPlayerSendMessagePacket.class, packet -> {
            UUID uuid = packet.getUuid();
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
            if (player != null) {
                player.sendMessage(TextComponent.fromLegacyText(packet.getMessage()));
            }
        });

        PacketMessenger.registerHandler(request -> {
            if (request.getKey().equalsIgnoreCase("player-settings")) {

                JsonData data = request.getData();
                UUID uniqueId = UUID.fromString(data.getString("uniqueId"));

                IPlayerSettings settings = ((PoloPluginBungeeBridge) PoloCloudAPI.getInstance().getPoloBridge()).getSettings(uniqueId);

                if (settings == null) {
                    request.respond(ResponseState.NULL);
                } else {
                    request.respond(new Response(new JsonData("settings", settings), ResponseState.SUCCESS));
                }

            }
        });

        //Unregister server handler
        PoloCloudAPI.getInstance().registerSimplePacketHandler(GameServerUnregisterPacket.class , packet -> ProxyServer.getInstance().getServers().remove(packet.getName()));

        //Update tab handler
        PoloCloudAPI.getInstance().registerSimplePacketHandler(ProxyTablistUpdatePacket.class, packet -> {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getUuid());
            if (player == null) {
                return;
            }
            player.setTabHeader(new TextComponent(packet.getHeader()), new TextComponent(packet.getFooter()));
        });

    }
}
