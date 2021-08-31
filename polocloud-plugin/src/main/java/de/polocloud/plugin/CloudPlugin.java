package de.polocloud.plugin;

import com.google.inject.Injector;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.packets.api.other.CacheRequestPacket;
import de.polocloud.api.network.packets.api.other.GlobalCachePacket;
import de.polocloud.api.network.packets.gameserver.GameServerSuccessfullyStartedPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.network.request.base.component.PoloComponent;
import de.polocloud.api.network.request.base.future.PoloFuture;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.pubsub.SimplePubSubManager;

import de.polocloud.plugin.bootstrap.IBootstrap;
import de.polocloud.plugin.bootstrap.proxy.commands.CloudCommand;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.property.GameServerProperty;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;

public class CloudPlugin extends PoloCloudAPI {

    //The bootstrap
    private final IBootstrap bootstrap;

    //Other fields
    private final GameServerProperty gameServerProperty;
    private final NetworkClient networkClient;
    //Managers
    private final IPubSubManager pubSubManager;


    public CloudPlugin(IBootstrap bootstrap) {
        super(bootstrap.getType());

        this.bootstrap = bootstrap;
        this.gameServerProperty = new GameServerProperty();
        this.networkClient = new NetworkClient(bootstrap);
        this.pubSubManager = new SimplePubSubManager(networkClient);

    }

    @Override
    public INetworkConnection getConnection() {
        return this.networkClient;
    }

    public static CloudPlugin getCloudPluginInstance() {
        return (CloudPlugin) PoloCloudAPI.getInstance();
    }

    public void onEnable() {
        if (getType() == PoloType.PLUGIN_PROXY) {
            PoloCloudAPI.getInstance().getCommandManager().setFilter(null);
            PoloCloudAPI.getInstance().getCommandManager().registerCommand(new CloudCommand());
        }
        final boolean[] received = {false};
        this.networkClient.getProtocol().registerPacketHandler(new IPacketHandler<GlobalCachePacket>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, GlobalCachePacket packet) {
                PoloCloudAPI.getInstance().setCache(packet.getMasterCache());

                if (!received[0]) {
                    received[0] = true;
                    try {
                        IGameServer thisService = gameServerManager.getThisService();
                        if (thisService == null) {
                            System.out.println("[CloudPlugin] Ran into a massive error! MasterCache doesn't contain current GameServer! Couldn't fully register " + getName() + "!");
                            return;
                        }
                        System.out.println("[CloudPlugin] Recognized this GameServer as '" + thisService.getName() + "' (" + thisService.getTemplate().getTemplateType().getDisplayName() + ") !");

                        bootstrap.registerListeners();
                        thisService.setStatus(GameServerStatus.RUNNING);
                        thisService.updateInternally();

                        networkClient.sendPacket(new GameServerSuccessfullyStartedPacket(thisService.getName(), thisService.getSnowflake()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return GlobalCachePacket.class;
            }
        });
        this.networkClient.connect(bootstrap.getPort(), nettyClient -> {
            System.out.println("[CloudPlugin] " + getName() + " successfully connected to CloudSystem! (" + nettyClient.getConnectedAddress() + ")");

            PoloFuture<String> query = PoloComponent.request(String.class, "polo::api::service::request::register").query();
            String response = query.timeOut(80L, "The Handshake-Request seemed to timed out!").pullValue();

            if (!query.isSuccess()) {
                System.out.println("[CloudPlugin] Couldn't get Handshake-Response within 80 ticks...");
            } else {
                System.out.println("[CloudPlugin] " + response);
            }
        });
    }

    @Override
    public void receivePacket(Packet packet) {
        networkClient.getProtocol().firePacketHandlers(networkClient.ctx(), packet);
    }

    @Override
    public void reload() {
        updateCache();
    }

    public JsonData getJson() {
        try {
            File parentFile = new File(NetworkClient.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile();
            return new JsonData(new File(parentFile + "/PoloCloud.json"));
        } catch (Exception e) {
            return null;
        }
    }

    public String getMasterAddress() {
        JsonData jsonData = getJson() == null ? new JsonData() : getJson();

        return jsonData.fallback("127.0.0.1").getString("Master-Address");
    }
    @Override
    public boolean terminate() {
        this.loggerFactory.shutdown(() -> {

        });
        return true;
    }

    @Override
    public void updateCache() {
        sendPacket(new CacheRequestPacket());
    }

    public NetworkClient getNetworkClient() {
        return networkClient;
    }

    public IBootstrap getBootstrap() {
        return bootstrap;
    }

    public GameServerProperty getGameServerProperty() {
        return gameServerProperty;
    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return null;
    }

    @Override
    public ICloudPlayerManager getCloudPlayerManager() {
        return cloudPlayerManager;
    }

    @Override
    public IPubSubManager getPubSubManager() {
        return pubSubManager;
    }

    @Override
    public Injector getGuice() {
        return null;
    }

    @Override
    public String getName() {
        return getJson().getString("GameServer-Name");
    }
}
