package de.polocloud.api.network.protocol.packet;

import de.polocloud.api.network.protocol.packet.api.EventPacket;
import de.polocloud.api.network.protocol.packet.api.other.CacheRequestPacket;
import de.polocloud.api.network.protocol.packet.api.other.GlobalCachePacket;
import de.polocloud.api.network.protocol.packet.api.PublishPacket;
import de.polocloud.api.network.protocol.packet.api.SubscribePacket;
import de.polocloud.api.network.protocol.packet.api.cloudplayer.APIRequestCloudPlayerPacket;
import de.polocloud.api.network.protocol.packet.api.cloudplayer.APIResponseCloudPlayerPacket;
import de.polocloud.api.network.protocol.packet.api.fallback.APIRequestPlayerMoveFallbackPacket;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerCopyPacket;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerCopyResponsePacket;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerPacket;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIResponseGameServerPacket;
import de.polocloud.api.network.protocol.packet.api.template.APIRequestTemplatePacket;
import de.polocloud.api.network.protocol.packet.api.template.APIResponseTemplatePacket;
import de.polocloud.api.network.protocol.packet.cloudplayer.CloudPlayerRegisterPacket;
import de.polocloud.api.network.protocol.packet.cloudplayer.CloudPlayerUnregisterPacket;
import de.polocloud.api.network.protocol.packet.command.CommandListAcceptorPacket;
import de.polocloud.api.network.protocol.packet.gameserver.*;
import de.polocloud.api.network.protocol.packet.gameserver.permissions.PermissionCheckResponsePacket;
import de.polocloud.api.network.protocol.packet.gameserver.proxy.ProxyTablistUpdatePacket;
import de.polocloud.api.network.protocol.packet.master.*;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperCachePacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperLoginPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRegisterStaticServerPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRequestShutdownPacket;
import de.polocloud.api.util.PoloUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PacketRegistry {

    private static Map<Integer, Class<? extends Packet>> packetMap = new ConcurrentHashMap<>();

    public static void registerPacket(int id, Class<? extends Packet> packet) {
        packetMap.put(id, packet);
    }

    public static void registerDefaultInternalPackets() {
        registerPackets(
            APIResponseGameServerPacket.class,
            APIRequestGameServerPacket.class,
            APIRequestCloudPlayerPacket.class,
            APIResponseCloudPlayerPacket.class,
            APIRequestTemplatePacket.class,
            APIResponseTemplatePacket.class,
            APIRequestPlayerMoveFallbackPacket.class,
            APIRequestGameServerCopyPacket.class,
            APIRequestGameServerCopyResponsePacket.class,

            GameServerSuccessfullyStartedPacket.class,
            GameServerUpdatePacket.class,
            GameServerControlPlayerPacket.class,
            GameServerExecuteCommandPacket.class,
            GameServerPlayerDisconnectPacket.class,
            GameServerPlayerRequestJoinPacket.class,
            GameServerPlayerUpdatePacket.class,
            GameServerRegisterPacket.class,
            GameServerShutdownPacket.class,
            GameServerUnregisterPacket.class,
            GameServerCloudCommandExecutePacket.class,

            CloudPlayerRegisterPacket.class,
            CloudPlayerUnregisterPacket.class,

            MasterPlayerKickPacket.class,
            MasterLoginResponsePacket.class,
            MasterPlayerRequestJoinResponsePacket.class,
            MasterRequestServerListUpdatePacket.class,
            MasterRequestServerStartPacket.class,
            MasterPlayerSendMessagePacket.class,
            MasterPlayerSendToServerPacket.class,
            MasterRequestsServerTerminatePacket.class,
            MasterUpdatePlayerInfoPacket.class,
            MasterStartServerPacket.class,

            WrapperLoginPacket.class,
            WrapperRegisterStaticServerPacket.class,
            WrapperRequestShutdownPacket.class,
            WrapperCachePacket.class,

            PublishPacket.class,
            SubscribePacket.class,
            EventPacket.class,
            GlobalCachePacket.class,
            CacheRequestPacket.class,

            PermissionCheckResponsePacket.class,
            ProxyTablistUpdatePacket.class,
            RedirectPacket.class,
            ForwardingPacket.class,
            CommandListAcceptorPacket.class);
    }

    @SafeVarargs
    private static void registerPackets(Class<? extends Packet>... packets) {
        for (int i = 0; i < packets.length; i++) {
            registerPacket(i + 1, packets[i]);
        }
    }

    public static int getPacketId(Class<? extends Packet> clazz) {
        return packetMap.keySet().stream().filter(id -> packetMap.get(id).equals(clazz)).findAny().orElse(-1);
    }

    public static Packet createPacket(int id)  {
        try {
            return packetMap.get(id).newInstance();
        } catch (Exception e) {
            return PoloUtils.getInstance(packetMap.get(id));
        }
    }

}
