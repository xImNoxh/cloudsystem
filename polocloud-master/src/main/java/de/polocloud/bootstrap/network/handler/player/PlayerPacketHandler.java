package de.polocloud.bootstrap.network.handler.player;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.packet.api.cloudplayer.APIRequestCloudPlayerPacket;
import de.polocloud.api.network.protocol.packet.api.cloudplayer.APIResponseCloudPlayerPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerCloudCommandExecutePacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.bootstrap.network.SimplePacketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PlayerPacketHandler extends PlayerPacketServiceController {

    @Inject
    private ICloudPlayerManager playerManager;

    public PlayerPacketHandler() {

        /**
         *  if game server execute ingame command
         *
         */
        new SimplePacketHandler<GameServerCloudCommandExecutePacket>(GameServerCloudCommandExecutePacket.class, packet ->
            executeICloudPlayerCommand(packet, (cloudCommands, strings) -> getPossibleCommand(strings).forEach(it ->
            executeCommand(playerManager, it, packet.getUuid(), strings))));

        new SimplePacketHandler<APIRequestCloudPlayerPacket>(APIRequestCloudPlayerPacket.class, (ctx, packet) -> {
            sendICloudPlayerAPIResponse(playerManager, ctx, packet);
        });
    }
}
