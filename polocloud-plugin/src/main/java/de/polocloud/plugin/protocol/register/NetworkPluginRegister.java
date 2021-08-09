package de.polocloud.plugin.protocol.register;

import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.ServiceVisibility;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.RedirectPacket;
import de.polocloud.api.network.protocol.packet.api.cloudplayer.APIResponseCloudPlayerPacket;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIResponseGameServerPacket;
import de.polocloud.api.network.protocol.packet.api.template.APIResponseTemplatePacket;
import de.polocloud.api.network.protocol.packet.gameserver.*;
import de.polocloud.api.network.response.ResponseHandler;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.pubsub.SimplePubSubManager;
import de.polocloud.api.template.ITemplate;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.api.CloudExecutor;
import de.polocloud.plugin.function.BootstrapFunction;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.NetworkRegister;
import de.polocloud.plugin.protocol.property.Property;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class NetworkPluginRegister extends NetworkRegister {

    private BootstrapFunction bootstrapFunction;

    public NetworkPluginRegister(NetworkClient networkClient, BootstrapFunction bootstrapFunction) {
        super(networkClient);

        this.bootstrapFunction = bootstrapFunction;

        CloudPlugin.getInstance().setProtocol(getNetworkClient().getClient().getProtocol());
        CloudPlugin.getInstance().setPubSubManager( new SimplePubSubManager(getNetworkClient(), CloudPlugin.getInstance().getCloudProtocol()));


        register((channelHandlerContext, packet) -> {
            GameServerMaxPlayersUpdatePacket object = (GameServerMaxPlayersUpdatePacket) packet;

            CloudPlugin.getInstance().getProperty().getProperties().put(Property.MAX_PLAYERS_MESSAGE, object.getMessage());
            CloudPlugin.getInstance().getProperty().getProperties().put(Property.MAX_PLAYERS_STATE, object.getMaxPlayers());
        }, GameServerMaxPlayersUpdatePacket.class)

            .register((channelHandlerContext, packet) -> {
                APIResponseCloudPlayerPacket object = (APIResponseCloudPlayerPacket) packet;
                UUID requestId = object.getRequestId();
                List<ICloudPlayer> response = object.getResponse();
                CompletableFuture<Object> completableFuture = ResponseHandler.getCompletableFuture(requestId, true);
                if (object.getType() == APIResponseCloudPlayerPacket.Type.SINGLE) {
                    completableFuture.complete(response.get(0));
                } else if (object.getType() == APIResponseCloudPlayerPacket.Type.LIST) {
                    completableFuture.complete(response);
                } else if (object.getType() == APIResponseCloudPlayerPacket.Type.BOOLEAN) {
                    completableFuture.complete(!response.isEmpty());
                }
            }, APIResponseCloudPlayerPacket.class)

            .register((channelHandlerContext, packet) -> {
                APIResponseGameServerPacket object = (APIResponseGameServerPacket) packet;

                UUID requestId = object.getRequestId();
                List<IGameServer> tmp = object.getResponse();
                List<IGameServer> response = new ArrayList<>();
                CompletableFuture<Object> completableFuture = ResponseHandler.getCompletableFuture(requestId, true);

                for (IGameServer gameserver : tmp) {
                    response.add(new IGameServer() {
                        @Override
                        public String getName() {
                            return gameserver.getName();
                        }

                        @Override
                        public GameServerStatus getStatus() {
                            return gameserver.getStatus();
                        }

                        @Override
                        public void setStatus(GameServerStatus status) {
                            throw new NotImplementedException();
                        }

                        @Override
                        public long getSnowflake() {
                            return gameserver.getSnowflake();
                        }

                        @Override
                        public ITemplate getTemplate() {
                            return gameserver.getTemplate();
                        }

                        @Override
                        public List<ICloudPlayer> getCloudPlayers() {
                            return gameserver.getCloudPlayers();
                        }

                        @Override
                        public long getTotalMemory() {
                            return gameserver.getTotalMemory();
                        }

                        @Override
                        public int getOnlinePlayers() {
                            return gameserver.getOnlinePlayers();
                        }

                        @Override
                        public int getPort() {
                            return gameserver.getPort();
                        }

                        @Override
                        public long getPing() {
                            return gameserver.getPing();
                        }

                        @Override
                        public long getStartTime() {
                            return gameserver.getStartTime();
                        }

                        @Override
                        public void stop() {
                            sendPacket(new GameServerShutdownPacket(gameserver.getName()));
                        }

                        @Override
                        public void terminate() {
                            gameserver.terminate();
                        }

                        @Override
                        public void sendPacket(Packet packet) {
                            channelHandlerContext.writeAndFlush(new RedirectPacket(getSnowflake(), packet));
                        }

                        @Override
                        public String getMotd() {
                            return gameserver.getMotd();
                        }

                        @Override
                        public void setMotd(String motd) {
                            sendPacket(new GameServerMotdUpdatePacket(motd));
                        }

                        @Override
                        public int getMaxPlayers() {
                            return gameserver.getMaxPlayers();
                        }

                        @Override
                        public void setMaxPlayers(int players) {
                            throw new NotImplementedException();
                        }

                        @Override
                        public void setVisible(ServiceVisibility serviceVisibility) {
                            throw new NotImplementedException();
                        }

                        @Override
                        public ServiceVisibility getServiceVisibility() {
                            return gameserver.getServiceVisibility();
                        }
                    });
                }

                completableFuture.complete((object.getType() == APIResponseGameServerPacket.Type.SINGLE ? response.get(0) : response));
            }, APIResponseGameServerPacket.class)

            .register((channelHandlerContext, packet) -> {
                APIResponseTemplatePacket object = (APIResponseTemplatePacket) packet;
                List<ITemplate> response = object.getResponse().stream().collect(Collectors.toList());

                ResponseHandler.getCompletableFuture(object.getRequestId(), true).complete(
                    object.getType() == APIResponseTemplatePacket.Type.SINGLE ? response.get(0) : response);
            }, APIResponseTemplatePacket.class)

            .register((channelHandlerContext, packet) -> {
                GameServerMaintenanceUpdatePacket object = (GameServerMaintenanceUpdatePacket) packet;
                CloudPlugin.getInstance().getProperty().getProperties().put(Property.MAINTENANCE_STATE, object.isState());
                CloudPlugin.getInstance().getProperty().getProperties().put(Property.MAINTENANCE_MESSAGE, object.getMessage());
            }, GameServerMaintenanceUpdatePacket.class);


        registerGameServerExecutePacket();
        registerGameServerShutdownPacket();
        registerGameServerMotdUpdatePacket();

    }


    public void registerGameServerExecutePacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                GameServerExecuteCommandPacket gameServerExecutePacket = (GameServerExecuteCommandPacket) obj;
                bootstrapFunction.executeCommand(gameServerExecutePacket.getCommand());
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return GameServerExecuteCommandPacket.class;
            }
        });
    }

    public void registerGameServerShutdownPacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                bootstrapFunction.shutdown();
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return GameServerShutdownPacket.class;
            }
        });
    }

    public void registerGameServerMotdUpdatePacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                GameServerMotdUpdatePacket packet = (GameServerMotdUpdatePacket) obj;
                CloudPlugin.getInstance().getProperty().getProperties().put(Property.MOTD, packet.getMotd());
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return GameServerMotdUpdatePacket.class;
            }
        });
    }


}
