package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.api.APIRequestCloudPlayerPacket;
import de.polocloud.api.network.protocol.packet.api.APIResponseCloudPlayerPacket;
import de.polocloud.api.network.protocol.packet.api.APIResponseGameServerPacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import io.netty.channel.ChannelHandlerContext;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class APIRequestCloudPlayerListener extends IPacketHandler {

    @Inject
    private ICloudPlayerManager cloudPlayerManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        System.out.println("try handle APIRequestCloudPlayer");
        APIRequestCloudPlayerPacket packet = (APIRequestCloudPlayerPacket) obj;

        UUID requestId = packet.getRequestId();
        APIRequestCloudPlayerPacket.Action action = packet.getAction();
        String value = packet.getValue();

        List<ICloudPlayer> responseData = new ArrayList<>();
        APIResponseCloudPlayerPacket.Type type = null;

        try {

            if (action == APIRequestCloudPlayerPacket.Action.ALL) {

                responseData = cloudPlayerManager.getAllOnlinePlayers().get();
                type = APIResponseCloudPlayerPacket.Type.LIST;

            } else if (action == APIRequestCloudPlayerPacket.Action.BY_NAME) {

                responseData.add(cloudPlayerManager.getOnlinePlayer(value).get());
                type = APIResponseCloudPlayerPacket.Type.SINGLE;

            } else if (action == APIRequestCloudPlayerPacket.Action.BY_UUID) {

                responseData.add(cloudPlayerManager.getOnlinePlayer(UUID.fromString(value)).get());
                type = APIResponseCloudPlayerPacket.Type.SINGLE;

            } else if (action == APIRequestCloudPlayerPacket.Action.ONLINE_UUID) {

                responseData.add(cloudPlayerManager.getOnlinePlayer(UUID.fromString(value)).get());
                type = APIResponseCloudPlayerPacket.Type.BOOLEAN;

            } else if (action == APIRequestCloudPlayerPacket.Action.ONLINE_NAME) {

                responseData.add(cloudPlayerManager.getOnlinePlayer(value).get());
                type = APIResponseCloudPlayerPacket.Type.BOOLEAN;

            }

            System.out.println("response " + responseData + " - " + type);

            ctx.writeAndFlush(new APIResponseCloudPlayerPacket(requestId, responseData, type));


        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return APIRequestCloudPlayerPacket.class;
    }
}
