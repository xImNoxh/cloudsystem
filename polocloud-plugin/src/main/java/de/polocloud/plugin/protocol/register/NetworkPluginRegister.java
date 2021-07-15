package de.polocloud.plugin.protocol.register;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.api.APIResponseGameServerPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerExecuteCommandPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerMaintenanceUpdatePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerMaxPlayersUpdatePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerShutdownPacket;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.api.CloudExecutor;
import de.polocloud.plugin.api.server.APIGameServerManager;
import de.polocloud.plugin.function.BootstrapFunction;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.NetworkRegister;
import de.polocloud.plugin.protocol.maintenance.MaintenanceState;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NetworkPluginRegister extends NetworkRegister {

    private BootstrapFunction bootstrapFunction;

    public NetworkPluginRegister(NetworkClient networkClient, BootstrapFunction bootstrapFunction) {
        super(networkClient);

        this.bootstrapFunction = bootstrapFunction;

        enableCloudExecutor();

        registerGameServerExecutePacket();
        registerMaintenanceStatePacket();
        registerGameServerShutdownPacket();
        registerMaxPlayersUpdatePacket();
        registerAPIHandler();

    }

    private void enableCloudExecutor() {
        new CloudExecutor(getNetworkClient(), getNetworkClient().getClient().getProtocol());
    }

    public void registerMaxPlayersUpdatePacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                GameServerMaxPlayersUpdatePacket packet = (GameServerMaxPlayersUpdatePacket) obj;
                CloudPlugin.getInstance().getMaxPlayerProperty().setMaxPlayers(packet.getMaxPlayers());
                CloudPlugin.getInstance().getMaxPlayerProperty().setMessage(packet.getMessage());
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return GameServerMaxPlayersUpdatePacket.class;
            }
        });
    }

    public void registerAPIHandler() {
        getNetworkClient().registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                System.out.println("Help me!");
                APIResponseGameServerPacket packet = (APIResponseGameServerPacket) obj;

                UUID requestId = packet.getRequestId();
                List<IGameServer> response = packet.getResponse();
                CompletableFuture<Object> completableFuture = ((APIGameServerManager) CloudExecutor.getInstance().getGameServerManager()).getCompletableFuture(requestId, true);

                if (packet.getType() == APIResponseGameServerPacket.Type.SINGLE) {
                    completableFuture.complete(response.get(0));
                } else {
                    completableFuture.complete(response);
                }

            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return APIResponseGameServerPacket.class;
            }
        });

    }

    public void registerMaintenanceStatePacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                GameServerMaintenanceUpdatePacket maintenanceUpdatePacket = (GameServerMaintenanceUpdatePacket) obj;
                CloudPlugin.getInstance().setState(new MaintenanceState(maintenanceUpdatePacket.isState(), maintenanceUpdatePacket.getMessage()));
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return GameServerMaintenanceUpdatePacket.class;
            }
        });
    }

    public void registerGameServerExecutePacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler() {
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
        getNetworkClient().registerPacketHandler(new IPacketHandler() {
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


}
