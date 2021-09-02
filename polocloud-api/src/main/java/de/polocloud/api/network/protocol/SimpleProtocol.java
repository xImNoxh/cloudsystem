package de.polocloud.api.network.protocol;

import com.google.inject.Inject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.base.other.ForwardingPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.api.wrapper.IWrapperManager;
import io.netty.channel.ChannelHandlerContext;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleProtocol implements IProtocol {


    @Inject
    @Named(value = "setting_protocol_threadSize")
    private int threadSize = 8;

    /**
     * All registered {@link IPacketHandler}s
     */
    private final Map<Class<? extends Packet>, List<IPacketHandler<? extends Packet>>> packetHandlers;

    public SimpleProtocol() {
        this.packetHandlers = new HashMap<>();

        this.registerPacketHandler(new IPacketHandler<ForwardingPacket>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, ForwardingPacket obj) {
                String receiver = obj.getReceiver();
                Packet forwardingPacket = obj.getPacket();
                switch (obj.getType()) {
                    case WRAPPER:
                        if (PoloCloudAPI.getInstance().getType() == PoloType.WRAPPER) {
                            PoloCloudAPI.getInstance().getConnection().getProtocol().firePacketHandlers(ctx, forwardingPacket);
                            return;
                        }
                        IWrapperManager wrapperManager = PoloCloudAPI.getInstance().getWrapperManager();
                        IWrapper wrapper = wrapperManager.getWrapper(receiver);
                        wrapper.sendPacket(forwardingPacket);
                        break;
                    case MASTER:
                        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
                            PoloCloudAPI.getInstance().getConnection().getProtocol().firePacketHandlers(ctx, forwardingPacket);
                            return;
                        } else {
                            PoloCloudAPI.getInstance().sendPacket(obj);
                        }
                        break;
                    case PLUGIN_SPIGOT:
                    case PLUGIN_PROXY:
                    case GENERAL_GAMESERVER:
                        if (PoloCloudAPI.getInstance().getType().isPlugin()) {
                            PoloCloudAPI.getInstance().getConnection().getProtocol().firePacketHandlers(ctx, forwardingPacket);
                            return;
                        }
                        IGameServerManager gameServerManager = PoloCloudAPI.getInstance().getGameServerManager();
                        gameServerManager.getCached(receiver).sendPacket(forwardingPacket);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return ForwardingPacket.class;
            }
        });
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
        if (packetHandlers.containsKey(packet.getClass())) {

            List<IPacketHandler<? extends Packet>> iPacketHandlers = packetHandlers.get(packet.getClass());
            for (IPacketHandler iPacketHandler : new ArrayList<>(iPacketHandlers)) {
                iPacketHandler.handlePacket(ctx, packet);
            }
        }
    }

}

