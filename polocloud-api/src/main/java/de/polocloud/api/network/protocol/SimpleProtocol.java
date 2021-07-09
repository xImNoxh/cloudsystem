package de.polocloud.api.network.protocol;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.packet.IPacket;
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


    private Map<Class<? extends IPacket>, List<IPacketHandler>> packetHandlerMap = new HashMap<>();


    @Override
    public void registerPacketHandler(IPacketHandler packetHandler) {
        List<IPacketHandler> list;

        if (packetHandlerMap.containsKey(packetHandler.getPacketClass())) {
            list = packetHandlerMap.get(packetHandler.getPacketClass());
        } else {
            list = new ArrayList<>();
        }

        list.add(packetHandler);

        packetHandlerMap.put(packetHandler.getPacketClass(), list);

    }

    @Override
    public void firePacketHandlers(ChannelHandlerContext ctx, IPacket packet) {
        if (packetHandlerMap.containsKey(packet.getClass())) {
            List<IPacketHandler> iPacketHandlers = packetHandlerMap.get(packet.getClass());

            for (IPacketHandler iPacketHandler : iPacketHandlers) {
                iPacketHandler.handlePacket(ctx, packet);
            }
        }
    }

}
