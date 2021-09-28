package de.polocloud.plugin.bootstrap.proxy.velocity.register;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
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
import de.polocloud.plugin.bootstrap.proxy.velocity.VelocityBootstrap;
import net.kyori.adventure.text.Component;

import java.net.InetSocketAddress;
import java.util.UUID;

public class VelocityPacketRegister {

    public VelocityPacketRegister() {

        ProxyServer proxyServer = VelocityBootstrap.getInstance().getServer();
        //Cache updating handler
        PoloCloudAPI.getInstance().registerSimplePacketHandler(GlobalCachePacket.class, globalCachePacket -> {

            for (IGameServer gameServer : globalCachePacket.getMasterCache().getGameServers()) {
                if (gameServer.getTemplate().getTemplateType() == TemplateType.PROXY) {
                    continue;
                }

                proxyServer.registerServer(new ServerInfo(gameServer.getName(), new InetSocketAddress(gameServer.getHost(), gameServer.getPort())));

            }
        });

        //Connecting to server handler
        PoloCloudAPI.getInstance().registerSimplePacketHandler(MasterPlayerSendToServerPacket.class, packet -> {
            UUID uuid = packet.getUuid();
            if (proxyServer.getPlayer(uuid).orElse(null) != null) {
                proxyServer.getServer(packet.getTargetServer()).ifPresent(registeredServer -> VelocityBootstrap.getInstance().getBridge().connect(uuid, registeredServer.getServerInfo().getName()));
            }
        });

        //Messaging handler
        PoloCloudAPI.getInstance().registerSimplePacketHandler(MasterPlayerSendMessagePacket.class, packet -> {
            UUID uuid = packet.getUuid();
            proxyServer.getPlayer(uuid).ifPresent(player -> player.sendMessage(Component.text(packet.getMessage())));
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
        PoloCloudAPI.getInstance().registerSimplePacketHandler(GameServerUnregisterPacket.class , packet -> proxyServer.unregisterServer(proxyServer.getServer(packet.getName()).orElse(null).getServerInfo()));

        //Update tab handler
        PoloCloudAPI.getInstance().registerSimplePacketHandler(ProxyTablistUpdatePacket.class, packet -> PoloCloudAPI.getInstance().getPoloBridge().sendTabList(packet.getUuid(), packet.getHeader(), packet.getFooter()));

    }
}
