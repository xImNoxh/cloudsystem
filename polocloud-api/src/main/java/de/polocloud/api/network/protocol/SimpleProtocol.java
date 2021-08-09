package de.polocloud.api.network.protocol;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.packet.Packet;
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


    private Map<Class<? extends Packet>, List<IPacketHandler<Packet>>> packetHandlerMap = new HashMap<>();
    
    @Override
    public void registerPacketHandler(IPacketHandler<Packet> packetHandler) {
        List<IPacketHandler<Packet>> list;

        if (packetHandlerMap.containsKey(packetHandler.getPacketClass())) {
            list = packetHandlerMap.get(packetHandler.getPacketClass());
        } else {
            list = new ArrayList<>();
        }

        list.add(packetHandler);

        packetHandlerMap.put(packetHandler.getPacketClass(), list);

    }

    @Override
    public void firePacketHandlers(ChannelHandlerContext ctx, Packet packet) {
        if (packetHandlerMap.containsKey(packet.getClass())) {
            List<IPacketHandler<Packet>> iPacketHandlers = packetHandlerMap.get(packet.getClass());

            for (IPacketHandler<Packet> iPacketHandler : iPacketHandlers) {
                iPacketHandler.handlePacket(ctx, packet);
            }
        }
    }

}
