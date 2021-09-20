package de.polocloud.plugin;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.packets.api.CacheRequestPacket;
import de.polocloud.api.network.packets.api.GlobalCachePacket;
import de.polocloud.api.network.packets.gameserver.GameServerSuccessfullyStartedPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.base.response.def.Response;
import de.polocloud.api.network.protocol.packet.base.response.base.IResponse;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.pubsub.SimplePubSubManager;

import de.polocloud.plugin.bootstrap.IBootstrap;
import de.polocloud.plugin.bootstrap.proxy.global.commands.CloudCommand;
import de.polocloud.plugin.protocol.NetworkClient;
import io.netty.channel.ChannelHandlerContext;
import jline.console.ConsoleReader;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class CloudPlugin extends PoloCloudAPI {

    //The bootstrap
    private final IBootstrap bootstrap;

    //Other fields
    private final NetworkClient networkClient;

    //Managers
    private final IPubSubManager pubSubManager;

    //If new server is allowed to start if full
    private boolean allowPercentage;

    public CloudPlugin(IBootstrap bootstrap) {
        super(bootstrap.getBridge().getEnvironment());

        this.allowPercentage = true;
        this.bootstrap = bootstrap;
        this.networkClient = new NetworkClient(bootstrap);
        this.pubSubManager = new SimplePubSubManager(networkClient);

        this.setPoloBridge(bootstrap.getBridge());

    }

    @Override
    public ConsoleReader getConsoleReader() {
        return null;
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
                        thisService.updateInternally();

                        networkClient.sendPacket(new GameServerSuccessfullyStartedPacket(thisService.getName(), thisService.getSnowflake(), bootstrap.getPort()));
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

            IResponse handshake =
                PacketMessenger
                    .create()
                    .blocking()
                    .timeOutAfter(TimeUnit.SECONDS, 4L)
                    .target(PoloType.MASTER)
                    .orElse(new Response("handshake", false))
                    .send("server-handshake", new JsonData("name", getName()));

            boolean hs = handshake.get("handshake").getAsBoolean();

            if (!hs) {
                System.out.println("=====================");
                System.out.println("[CloudPlugin] Couldn't get Authentication-Response within 4 Seconds...");
                System.out.println("[CloudPlugin] This does not have to be bad! Something bad could have happened while reading or writing the HandShake-Request!");
                System.out.println("[CloudPlugin] But if things start to not work and errors occur, then this is the reason why most of the time!");
                System.out.println("[CloudPlugin] You should report this error anyways because it should not appear");
                System.out.println("=====================");
            } else {
                System.out.println("[CloudPlugin] Authenticated this Server!");
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
            return new JsonData(new File(FileConstants.CLOUD_JSON_NAME));
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
        this.loggerFactory.shutdown();
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
    public String getName() {
        return getJson().getString("GameServer-Name");
    }
}
