package de.polocloud.api.network.protocol.packet;

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
import de.polocloud.api.network.protocol.packet.command.CommandListAcceptorPacket;
import de.polocloud.api.network.protocol.packet.gameserver.*;
import de.polocloud.api.network.protocol.packet.gameserver.permissions.PermissionCheckResponsePacket;
import de.polocloud.api.network.protocol.packet.gameserver.proxy.ProxyTablistUpdatePacket;
import de.polocloud.api.network.protocol.packet.master.*;
import de.polocloud.api.network.protocol.packet.statistics.StatisticPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperLoginPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRegisterStaticServerPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRequestShutdownPacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PacketRegistry {

    //private static List<Class<? extends Packet>> packetList = new ArrayList<>();

    private static Map<Integer, Class<? extends Packet>> packetMap = new ConcurrentHashMap<>();

    public static void registerPacket(int id, Class<? extends Packet> packet) {
        packetMap.put(id, packet);
    }

    public static void registerDefaultInternalPackets() {
        registerPacket(100, APIResponseGameServerPacket.class);
        registerPacket(101, APIRequestGameServerPacket.class);

        registerPacket(102, GameServerMotdUpdatePacket.class);
        registerPacket(103, GameServerControlPlayerPacket.class);
        registerPacket(104, GameServerExecuteCommandPacket.class);
        registerPacket(105, GameServerMaintenanceUpdatePacket.class);
        registerPacket(106, GameServerMaxPlayersUpdatePacket.class);
        registerPacket(107, GameServerPlayerDisconnectPacket.class);
        registerPacket(108, GameServerPlayerRequestJoinPacket.class);
        registerPacket(109, GameServerPlayerUpdatePacket.class);
        registerPacket(110, GameServerRegisterPacket.class);
        registerPacket(111, GameServerShutdownPacket.class);
        registerPacket(112, GameServerUnregisterPacket.class);

        registerPacket(113, MasterPlayerKickPacket.class);
        registerPacket(114, MasterLoginResponsePacket.class);
        registerPacket(115, MasterPlayerRequestJoinResponsePacket.class);
        registerPacket(116, MasterRequestServerListUpdatePacket.class);
        registerPacket(117, MasterRequestServerStartPacket.class);

        registerPacket(118, StatisticPacket.class);

        registerPacket(119, WrapperLoginPacket.class);

        registerPacket(120, PublishPacket.class);
        registerPacket(121, SubscribePacket.class);

        registerPacket(122, WrapperRegisterStaticServerPacket.class);

        registerPacket(123, MasterPlayerSendMessagePacket.class);
        registerPacket(124, MasterPlayerSendToServerPacket.class);

        registerPacket(125, APIRequestCloudPlayerPacket.class);
        registerPacket(126, APIResponseCloudPlayerPacket.class);

        registerPacket(127, GameServerCloudCommandExecutePacket.class);
        registerPacket(128, CommandListAcceptorPacket.class);

        registerPacket(129, PermissionCheckResponsePacket.class);
        registerPacket(130, ProxyTablistUpdatePacket.class);

        registerPacket(131, APIRequestTemplatePacket.class);
        registerPacket(132, APIResponseTemplatePacket.class);


        registerPacket(133, RedirectPacket.class);
        registerPacket(134, APIRequestPlayerMoveFallbackPacket.class);
        registerPacket(135, APIRequestGameServerCopyPacket.class);
        registerPacket(136, APIRequestGameServerCopyResponsePacket.class);
        registerPacket(137, WrapperRequestShutdownPacket.class);

        registerPacket(138, MasterRequestsServerTerminatePacket.class);
    }

    public static int getPacketId(Class<? extends Packet> clazz) {
        for (Integer id : packetMap.keySet()) {
            if (packetMap.get(id).equals(clazz)) {
                return id;
            }
        }
        return -1;
    }

    public static Packet createInstance(int id) throws InstantiationException, IllegalAccessException {
        return packetMap.get(id).newInstance();
    }

}
