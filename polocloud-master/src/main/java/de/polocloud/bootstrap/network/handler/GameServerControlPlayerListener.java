package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerControlPlayerPacket;
import de.polocloud.api.network.protocol.packet.master.MasterKickPlayerPacket;
import de.polocloud.api.player.ICloudPlayerManager;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class GameServerControlPlayerListener extends IPacketHandler {

    @Inject
    private ICloudPlayerManager playerManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {

        GameServerControlPlayerPacket packet = (GameServerControlPlayerPacket) obj;

        UUID uuid = packet.getUuid();
        if(!playerManager.isPlayerOnline(uuid)){
            //TODO ctx.writeAndFlush(new MasterKickPlayerPacket(uuid, "§cPlease connect to the Proxy!"));
        }

    }

    @Override
    public Class<? extends IPacket> getPacketClass() {
        return GameServerControlPlayerPacket.class;
    }
}
