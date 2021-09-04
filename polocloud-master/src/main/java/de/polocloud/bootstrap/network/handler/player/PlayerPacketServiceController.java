package de.polocloud.bootstrap.network.handler.player;

import com.google.common.collect.Lists;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.runner.ICommandRunner;
import de.polocloud.api.event.impl.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.impl.player.CloudPlayerJoinNetworkEvent;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.packets.api.cloudplayer.APIRequestCloudPlayerPacket;
import de.polocloud.api.network.packets.api.cloudplayer.APIResponseCloudPlayerPacket;

import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.pubsub.MasterPubSubManager;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.logger.log.types.ConsoleColors;
import io.netty.channel.ChannelHandlerContext;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public abstract class PlayerPacketServiceController {

    public List<ICloudPlayer> getICloudPlayerByPacketResponse(ICloudPlayerManager manager, APIRequestCloudPlayerPacket.Action action, String value)
        throws ExecutionException, InterruptedException {
        return isAllPacket(action) ? manager.getAllCached() : isNamePacket(action) ?
            Lists.newArrayList(manager.getCached(value)) : Lists.newArrayList(manager.getCached(UUID.fromString(value)));
    }

    public void sendICloudPlayerAPIResponse(ICloudPlayerManager manager, ChannelHandlerContext ctx, APIRequestCloudPlayerPacket packet) {
        try {
            ctx.writeAndFlush(new APIResponseCloudPlayerPacket(packet.getRequestId(), getICloudPlayerByPacketResponse(manager, packet.getAction(), packet.getValue())
                , getCloudPlayerTypeByPacketResponse(packet.getAction())));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void callConnectEvent(MasterPubSubManager pubSubManager, ICloudPlayer cloudPlayer) {
        pubSubManager.publish("polo:event:playerJoin", cloudPlayer.getName());
        PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerJoinNetworkEvent(cloudPlayer));
    }

    public void callDisconnectEvent(MasterPubSubManager pubSubManager, ICloudPlayer cloudPlayer) {
        pubSubManager.publish("polo:event:playerQuit", cloudPlayer.getUUID().toString());
        PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerDisconnectEvent(cloudPlayer));
    }


    private List<ICommandRunner> getCachedCommands() {
        return PoloCloudAPI.getInstance().getCommandManager().getCommands();
    }

    private boolean isCommandMatch(String key, String[] keys, String input) {
        return key.equalsIgnoreCase(input) || Arrays.stream(keys).anyMatch(it -> it.equalsIgnoreCase(input));
    }

    public APIResponseCloudPlayerPacket.Type getCloudPlayerTypeByPacketResponse(APIRequestCloudPlayerPacket.Action action) {
        return isAllPacket(action) ? APIResponseCloudPlayerPacket.Type.LIST : isSingleton(action) ? APIResponseCloudPlayerPacket.Type.SINGLE : APIResponseCloudPlayerPacket.Type.BOOLEAN;
    }

    public boolean isSingleton(APIRequestCloudPlayerPacket.Action action) {
        return action.equals(APIRequestCloudPlayerPacket.Action.BY_NAME) || action.equals(APIRequestCloudPlayerPacket.Action.BY_UUID);
    }

    public boolean isAllPacket(APIRequestCloudPlayerPacket.Action action) {
        return action.equals(APIRequestCloudPlayerPacket.Action.ALL);
    }

    public boolean isNamePacket(APIRequestCloudPlayerPacket.Action action) {
        return action.equals(APIRequestCloudPlayerPacket.Action.ONLINE_NAME) || action.equals(APIRequestCloudPlayerPacket.Action.BY_NAME);
    }
    
    public void sendConnectMessage(MasterConfig masterConfig, ICloudPlayer cloudPlayer) {
        if (masterConfig.getProperties().isLogPlayerConnections() && cloudPlayer != null && cloudPlayer.getProxyServer() != null)
            PoloLogger.print(LogLevel.INFO, "Player " + ConsoleColors.CYAN + cloudPlayer.getName() + ConsoleColors.GRAY +
                " is connected on " + cloudPlayer.getProxyServer().getName() + "!");
    }

    public void sendDisconnectMessage(MasterConfig masterConfig, ICloudPlayer cloudPlayer) {
        if (masterConfig.getProperties().isLogPlayerConnections())
            PoloLogger.print(LogLevel.INFO, "Player " + ConsoleColors.CYAN + cloudPlayer.getName() + ConsoleColors.GRAY + " is now disconnected!");
    }

}
