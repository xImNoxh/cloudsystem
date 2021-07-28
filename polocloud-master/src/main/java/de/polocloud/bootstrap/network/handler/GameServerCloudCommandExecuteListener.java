package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerCloudCommandExecutePacket;
import de.polocloud.api.player.ICloudPlayerManager;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class GameServerCloudCommandExecuteListener extends IPacketHandler<Packet> {

    @Inject
    private ICloudPlayerManager cloudPlayerManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        GameServerCloudCommandExecutePacket packet = (GameServerCloudCommandExecutePacket) obj;

        String[] args = packet.getCommand().split(" ");
        List<CloudCommand> commands = CloudAPI.getInstance().getCommandPool().getAllCachedCommands().stream().filter(key -> key.getName().equalsIgnoreCase(args[0])).collect(Collectors.toList());

        for (CloudCommand command : commands) {
            try {
                command.execute(cloudPlayerManager.getOnlinePlayer(packet.getUuid()).get(), args);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return GameServerCloudCommandExecutePacket.class;
    }
}
