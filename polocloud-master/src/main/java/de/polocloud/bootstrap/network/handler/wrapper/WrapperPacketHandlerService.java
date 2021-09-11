package de.polocloud.bootstrap.network.handler.wrapper;

import de.polocloud.api.network.packets.master.MasterLoginResponsePacket;
import de.polocloud.api.network.packets.wrapper.WrapperLoginPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import io.netty.channel.ChannelHandlerContext;

public class WrapperPacketHandlerService extends WrapperHandlerServiceController implements IPacketHandler<WrapperLoginPacket> {

    public WrapperPacketHandlerService() {

    }

    @Override
    public void handlePacket(ChannelHandlerContext ctx, WrapperLoginPacket packet) {
        getLoginResponse(packet, (response, client) -> {
            String name = packet.getName();
            if (wrapperManager.getWrapper(name) != null) {
                client.sendPacket(new MasterLoginResponsePacket(false, "§cThere is already a Wrapper with the name §e" + name + "§c!"));
            } else {
                client.sendPacket(getMasterLoginResponsePacket(response));
            }

            if (!response) {
                ctx.close();
                return;
            }

            wrapperManager.registerWrapper(client);
            sendWrapperSuccessfully(client, packet);
        }, ctx);
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return WrapperLoginPacket.class;
    }
}
