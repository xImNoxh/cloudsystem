package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerControlPlayerPacket;
import de.polocloud.api.player.ICloudPlayerManager;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GameServerControlPlayerListener extends IPacketHandler {

    @Inject
    private ICloudPlayerManager playerManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {

        GameServerControlPlayerPacket packet = (GameServerControlPlayerPacket) obj;

        UUID uuid = packet.getUuid();
        try {
            if(!playerManager.isPlayerOnline(uuid).get()){
                //TODO fix only proxy join ctx.writeAndFlush(new MasterKickPlayerPacket(uuid, "§cPlease connect to the Proxy!"));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return GameServerControlPlayerPacket.class;
    }
}
