package de.polocloud.bootstrap.network.handler.player;

import com.google.common.collect.Lists;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.runner.ICommandRunner;
import de.polocloud.api.event.impl.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.impl.player.CloudPlayerJoinNetworkEvent;
import de.polocloud.api.fallback.base.IFallback;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.packets.api.cloudplayer.APIRequestCloudPlayerPacket;
import de.polocloud.api.network.packets.api.cloudplayer.APIResponseCloudPlayerPacket;
import de.polocloud.api.network.packets.gameserver.GameServerCloudCommandExecutePacket;
import de.polocloud.api.network.packets.gameserver.GameServerPlayerDisconnectPacket;
import de.polocloud.api.network.packets.gameserver.GameServerPlayerRequestJoinPacket;
import de.polocloud.api.network.packets.master.MasterPlayerRequestJoinResponsePacket;
import de.polocloud.api.network.packets.master.MasterUpdatePlayerInfoPacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.pubsub.MasterPubSubManager;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.logger.log.types.ConsoleColors;
import io.netty.channel.ChannelHandlerContext;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class PlayerPacketServiceController {

    public void executeCommand(ICloudPlayerManager cloudPlayerManager, ICommandRunner command, UUID uuid, String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String arg : args) {
            stringBuilder.append(arg).append(" ");
        }
        PoloCloudAPI.getInstance().getCommandManager().runCommand(stringBuilder.toString(), cloudPlayerManager.getCachedObject(uuid));
    }

    public List<ICloudPlayer> getICloudPlayerByPacketResponse(ICloudPlayerManager manager, APIRequestCloudPlayerPacket.Action action, String value)
        throws ExecutionException, InterruptedException {
        return isAllPacket(action) ? manager.getAllCached() : isNamePacket(action) ?
            Lists.newArrayList(manager.getCached(value)) : Lists.newArrayList(manager.getCachedObject(UUID.fromString(value)));
    }

    public void sendICloudPlayerAPIResponse(ICloudPlayerManager manager, ChannelHandlerContext ctx, APIRequestCloudPlayerPacket packet) {
        try {
            ctx.writeAndFlush(new APIResponseCloudPlayerPacket(packet.getRequestId(), getICloudPlayerByPacketResponse(manager, packet.getAction(), packet.getValue())
                , getCloudPlayerTypeByPacketResponse(packet.getAction())));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getOnlinePlayer(GameServerPlayerDisconnectPacket packet, UUID uuid, ICloudPlayerManager playerManager, Consumer<ICloudPlayer> player) {
        if (playerManager.isPlayerOnline(uuid))
            player.accept(playerManager.getCachedObject(packet.getUuid()));
    }

    public void removeOnServerIfExist(ICloudPlayerManager playerManager, ICloudPlayer onlinePlayer) {
        convertService(onlinePlayer, (proxy, server) -> {
            playerManager.unregisterPlayer(onlinePlayer);
        });
    }

    public void updateProxyInfoService(IGameServerManager manager, ICloudPlayerManager playerManager) {
        List<IGameServer> gameServersByType = manager.getGameServersByType(TemplateType.PROXY);
        List<ICloudPlayer> players = playerManager.getAllCached();
        gameServersByType.forEach(it -> it.sendPacket(new MasterUpdatePlayerInfoPacket(players.size(), it.getTemplate().getMaxPlayers())));
    }

    public void callConnectEvent(MasterPubSubManager pubSubManager, ICloudPlayer cloudPlayer) {
        pubSubManager.publish("polo:event:playerJoin", cloudPlayer.getName());
        PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerJoinNetworkEvent(cloudPlayer));
    }

    public void callDisconnectEvent(MasterPubSubManager pubSubManager, ICloudPlayer cloudPlayer) {
        pubSubManager.publish("polo:event:playerQuit", cloudPlayer.getUUID().toString());
        PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerDisconnectEvent(cloudPlayer));

    }

    public List<ICommandRunner> getPossibleCommand(String[] args) {
        return getCachedCommands().stream().filter(key -> isCommandMatch(key.getCommand().name(), key.getCommand().aliases(), args[0])).collect(Collectors.toList());
    }

    private List<ICommandRunner> getCachedCommands() {
        return PoloCloudAPI.getInstance().getCommandManager().getCommands();
    }

    private boolean isCommandMatch(String key, String[] keys, String input) {
        return key.equalsIgnoreCase(input) || Arrays.stream(keys).anyMatch(it -> it.equalsIgnoreCase(input));
    }

    public void executeICloudPlayerCommand(GameServerCloudCommandExecutePacket packet, BiConsumer<List<ICommandRunner>, String[]> handling) {
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

    public void callCurrentServices(IGameServerManager serverManager, String snow, BiConsumer<IGameServer, IGameServer> service, ChannelHandlerContext ctx) {
        service.accept(serverManager.getCached(snow), serverManager.getCachedObject(ctx));
    }


    public boolean isGameServerListEmpty(List<IGameServer> list) {
        return list == null || list.isEmpty();
    }

    public void sendToFallback(ICloudPlayer player) {
        if (player != null) player.sendToFallback();
    }

    public void sendConnectMessage(MasterConfig masterConfig, ICloudPlayer cloudPlayer) {
        if (masterConfig.getProperties().isLogPlayerConnections())
            PoloLogger.print(LogLevel.INFO, "Player " + ConsoleColors.CYAN + cloudPlayer.getName() + ConsoleColors.GRAY +
                " is playing on " + cloudPlayer.getMinecraftServer().getName() + "(" + cloudPlayer.getProxyServer().getName() + ")");
    }

    public void sendDisconnectMessage(MasterConfig masterConfig, GameServerPlayerDisconnectPacket placket) {
        if (masterConfig.getProperties().isLogPlayerConnections())
            PoloLogger.print(LogLevel.INFO, "Player " + ConsoleColors.CYAN + placket.getName() + ConsoleColors.GRAY + " is now disconnected!");
    }

    public void sendMasterPlayerRequestJoinResponsePacket(ChannelHandlerContext ctx, UUID uuid, String serviceName, long snowflake) {
        ctx.writeAndFlush(new MasterPlayerRequestJoinResponsePacket(uuid, serviceName, snowflake));
    }

    public void getSearchedFallback(GameServerPlayerRequestJoinPacket packet, BiConsumer<IFallback, UUID> handle) {
        handle.accept(PoloCloudAPI.getInstance().getFallbackManager().getHighestFallback(null), packet.getUuid());
    }

    public void convertService(ICloudPlayer player, BiConsumer<Boolean, Boolean> online) {
        online.accept(player.getProxyServer() != null, player.getMinecraftServer() != null);
    }

}
