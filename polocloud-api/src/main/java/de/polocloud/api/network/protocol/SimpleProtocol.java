package de.polocloud.api.network.protocol;

import com.google.inject.Inject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.ForwardingPacket;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.wrapper.IWrapper;
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
    private final Map<Class<? extends Packet>, List<IPacketHandler<Packet>>> packetHandlers;

    public SimpleProtocol() {
        this.packetHandlers = new HashMap<>();

        this.registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                ForwardingPacket packet = (ForwardingPacket)obj;
                String receiver = packet.getReceiver();
                Packet forwardingPacket = packet.getPacket();
                switch (packet.getType()) {
                    case WRAPPER:
                        IWrapperManager wrapperManager = PoloCloudAPI.getInstance().getWrapperManager();
                        IWrapper wrapper = wrapperManager.getWrapper(receiver);
                        wrapper.sendPacket(forwardingPacket);
                        break;
                    case MASTER:
                        handlePacket(ctx, forwardingPacket);
                        break;
                    case PLUGIN_SPIGOT:
                    case PLUGIN_PROXY:
                    case GENERAL_GAMESERVER:
                        IGameServerManager gameServerManager = PoloCloudAPI.getInstance().getGameServerManager();
                        gameServerManager.getGameServerByName(receiver).thenAccept(gameServer -> gameServer.sendPacket(forwardingPacket));
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
    public void registerPacketHandler(IPacketHandler<Packet> packetHandler) {
        List<IPacketHandler<Packet>> list = packetHandlers.containsKey(packetHandler.getPacketClass()) ? packetHandlers.get(packetHandler.getPacketClass()) : new ArrayList<>();
        list.add(packetHandler);
        packetHandlers.put(packetHandler.getPacketClass(), list);

    }

    @Override
    public void firePacketHandlers(ChannelHandlerContext ctx, Packet packet) {
        if (packetHandlers.containsKey(packet.getClass())) {
            List<IPacketHandler<Packet>> iPacketHandlers = packetHandlers.get(packet.getClass());
            for (IPacketHandler<Packet> iPacketHandler : iPacketHandlers) {
                iPacketHandler.handlePacket(ctx, packet);
            }
        }
    }

}
