package de.polocloud.api.network.protocol;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.base.other.ForwardingPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.api.wrapper.IWrapperManager;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleProtocol implements IProtocol {

    /**
     * All registered {@link IPacketHandler}s
     */
    private final Map<Class<? extends Packet>, List<IPacketHandler<? extends Packet>>> packetHandlers;

    public SimpleProtocol() {
        this.packetHandlers = new HashMap<>();
    }

    @Override
    public void registerPacketHandler(IPacketHandler<? extends Packet> packetHandler) {
        List<IPacketHandler<? extends Packet>> list = packetHandlers.containsKey(packetHandler.getPacketClass()) ? packetHandlers.get(packetHandler.getPacketClass()) : new ArrayList<>();
        list.add(packetHandler);
        packetHandlers.put(packetHandler.getPacketClass(), list);

    }

    @Override
    public void unregisterPacketHandler(IPacketHandler<?> packetHandler) {
        List<IPacketHandler<? extends Packet>> list = packetHandlers.containsKey(packetHandler.getPacketClass()) ? packetHandlers.get(packetHandler.getPacketClass()) : new ArrayList<>();
        list.remove(packetHandler);
        packetHandlers.put(packetHandler.getPacketClass(), list);
    }

    @Override
    public void firePacketHandlers(ChannelHandlerContext ctx, Packet packet) {
        if (packet instanceof ForwardingPacket) {
            ForwardingPacket obj = (ForwardingPacket)packet;

            switch (obj.getType()) {

                case WRAPPER:
                    if (PoloCloudAPI.getInstance().getType() == PoloType.WRAPPER && obj.getReceiver().equalsIgnoreCase(PoloCloudAPI.getInstance().getName())) {
                        firePacketHandlers(ctx, obj.getPacket());
                        return;
                    } else if (PoloCloudAPI.getInstance().getType() != PoloType.WRAPPER) {
                        IWrapper wrapper = PoloCloudAPI.getInstance().getWrapperManager().getWrapper(obj.getReceiver());
                        wrapper.sendPacket(obj.getPacket());
                    }
                    break;

                case MASTER:
                    if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
                        firePacketHandlers(ctx, obj.getPacket());
                        return;
                    } else {
                        PoloCloudAPI.getInstance().sendPacket(obj);
                    }
                    break;
                case PLUGIN_SPIGOT:
                case PLUGIN_PROXY:
                case GENERAL_GAMESERVER:
                    if (PoloCloudAPI.getInstance().getType().isPlugin()) {
                        firePacketHandlers(ctx, obj.getPacket());
                        return;
                    }
                    IGameServerManager gameServerManager = PoloCloudAPI.getInstance().getGameServerManager();
                    gameServerManager.getCached(obj.getReceiver()).sendPacket(obj.getPacket());
                    break;
                default:
                    break;
            }
            return;
        }

        if (packetHandlers.containsKey(packet.getClass())) {

            List<IPacketHandler<? extends Packet>> iPacketHandlers = packetHandlers.get(packet.getClass());
            for (IPacketHandler iPacketHandler : new ArrayList<>(iPacketHandlers)) {
                iPacketHandler.handlePacket(ctx, packet);
            }
        }
    }

}

