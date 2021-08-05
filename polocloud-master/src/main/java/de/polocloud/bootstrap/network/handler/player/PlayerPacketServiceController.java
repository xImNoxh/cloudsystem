package de.polocloud.bootstrap.network.handler.player;

import com.google.common.collect.Lists;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.api.cloudplayer.APIRequestCloudPlayerPacket;
import de.polocloud.api.network.protocol.packet.api.cloudplayer.APIResponseCloudPlayerPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerCloudCommandExecutePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerRequestJoinPacket;
import de.polocloud.api.network.protocol.packet.master.MasterPlayerRequestJoinResponsePacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.bootstrap.Master;
import io.netty.channel.ChannelHandlerContext;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class PlayerPacketServiceController {

    public void executeCommand(ICloudPlayerManager cloudPlayerManager, CloudCommand command, UUID uuid, String[] args) {
        try {
            command.execute(cloudPlayerManager.getOnlinePlayer(uuid).get(), args);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public List<ICloudPlayer> getICloudPlayerByPacketResponse(ICloudPlayerManager manager, APIRequestCloudPlayerPacket.Action action, String value)
        throws ExecutionException, InterruptedException {
        return isAllPacket(action) ? manager.getAllOnlinePlayers().get() : isNamePacket(action) ?
            Lists.newArrayList(manager.getOnlinePlayer(value).get()) : Lists.newArrayList(manager.getOnlinePlayer(UUID.fromString(value)).get());
    }

    public void sendICloudPlayerAPIResponse(ICloudPlayerManager manager, ChannelHandlerContext ctx, APIRequestCloudPlayerPacket packet) {
        try {
            ctx.writeAndFlush(new APIResponseCloudPlayerPacket(packet.getRequestId(), getICloudPlayerByPacketResponse(manager, packet.getAction(), packet.getValue())
                , getCloudPlayerTypeByPacketResponse(packet.getAction())));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public List<CloudCommand> getPossibleCommand(String[] args) {
        return getCachedCommands().stream().filter(key -> isCommandMatch(key.getName(), key.getAliases(), args[0])).collect(Collectors.toList());
    }

    private List<CloudCommand> getCachedCommands() {
        return CloudAPI.getInstance().getCommandPool().getAllCachedCommands();
    }

    private boolean isCommandMatch(String key, String[] keys, String input) {
        return key.equalsIgnoreCase(input) || Arrays.stream(keys).anyMatch(it -> it.equalsIgnoreCase(input));
    }
    public void executeICloudPlayerCommand(GameServerCloudCommandExecutePacket packet, BiConsumer<List<CloudCommand>, String[]> handling) {
        handling.accept(getPossibleCommand(packet.getCommand().split(" ")), packet.getCommand().split(" "));
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

    public IGameServer getNextFallback(List<IGameServer> fallbacks){
        return fallbacks.stream().max(Comparator.comparingInt(IGameServer::getOnlinePlayers)).orElse(null);
    }

    public boolean isGameServerListEmpty(List<IGameServer> list) {
        return list == null || list.isEmpty();
    }

    public void sendToFallback(ICloudPlayer player){
        if (player != null) player.sendToFallback();
    }

    public void sendMasterPlayerRequestJoinResponsePacket(ChannelHandlerContext ctx, UUID uuid, String serviceName, long snowflake){
        ctx.writeAndFlush(new MasterPlayerRequestJoinResponsePacket(uuid, serviceName, snowflake));
    }

    public void getSearchedFallback(GameServerPlayerRequestJoinPacket packet, BiConsumer<List<IGameServer>, UUID> handle) {
        handle.accept(Master.getInstance().getFallbackSearchService().searchForTemplate(null, false), packet.getUuid());
    }

}
