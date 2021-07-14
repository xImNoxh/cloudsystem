package de.polocloud.api.network.protocol.packet;

import de.polocloud.api.network.protocol.packet.api.APIRequestGameServerPacket;
import de.polocloud.api.network.protocol.packet.api.APIResponseGameServerPacket;
import de.polocloud.api.network.protocol.packet.gameserver.*;
import de.polocloud.api.network.protocol.packet.gameserver.proxy.ProxyMotdUpdatePacket;
import de.polocloud.api.network.protocol.packet.master.*;
import de.polocloud.api.network.protocol.packet.statistics.StatisticPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperLoginPacket;

import java.util.ArrayList;
import java.util.List;

public class PacketRegistry {

    private static List<Class<? extends IPacket>> packetList = new ArrayList<>();

    public static void registerPacket(Class<? extends IPacket> packet) {
        packetList.add(packet);
    }

    public static void registerDefaultPackets(){
        registerPacket(APIResponseGameServerPacket.class);
        registerPacket(APIRequestGameServerPacket.class);

        registerPacket(ProxyMotdUpdatePacket.class);
        registerPacket(GameServerControlPlayerPacket.class);
        registerPacket(GameServerExecuteCommandPacket.class);
        registerPacket(GameServerMaintenanceUpdatePacket.class);
        registerPacket(GameServerPlayerDisconnectPacket.class);
        registerPacket(GameServerPlayerRequestJoinPacket.class);
        registerPacket(GameServerPlayerUpdatePacket.class);
        registerPacket(GameServerRegisterPacket.class);
        registerPacket(GameServerShutdownPacket.class);
        registerPacket(GameServerUnregisterPacket.class);

        registerPacket(MasterKickPlayerPacket.class);
        registerPacket(MasterLoginResponsePacket.class);
        registerPacket(MasterPlayerRequestResponsePacket.class);
        registerPacket(MasterRequestServerListUpdatePacket.class);
        registerPacket(MasterRequestServerStartPacket.class);

        registerPacket(StatisticPacket.class);

        registerPacket(WrapperLoginPacket.class);

    }

    public static int getPacketId(Class<? extends IPacket> clazz){
        return packetList.indexOf(clazz);
    }

    public static IPacket createInstance(int id) throws InstantiationException, IllegalAccessException {
        return packetList.get(id).newInstance();
    }

}
