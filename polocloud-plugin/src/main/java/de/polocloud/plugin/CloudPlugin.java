package de.polocloud.plugin;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.SimpleConsoleExecutor;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.packets.api.CacheRequestPacket;
import de.polocloud.api.network.packets.api.GlobalCachePacket;
import de.polocloud.api.network.packets.gameserver.GameServerSuccessfullyStartedPacket;
import de.polocloud.api.network.packets.master.MasterReportExceptionPacket;
import de.polocloud.api.network.packets.other.TextPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.base.response.def.Response;
import de.polocloud.api.network.protocol.packet.base.response.base.IResponse;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.pubsub.SimplePubSubManager;

import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.plugin.bootstrap.IBootstrap;
import de.polocloud.plugin.bootstrap.proxy.global.commands.CloudCommand;
import de.polocloud.plugin.protocol.NetworkClient;
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


    /**
     * Constructs a new instance of this {@link CloudPlugin}
     * using a specific {@link IBootstrap} for the provided
     * plugin instance (bukkit, proxy)
     * The plugin does not connect to the Cloud when constructing
     * therefore there is the Method {@link CloudPlugin#connectToCloud()}
     *
     * @param bootstrap the provided bootstrap
     */
    public CloudPlugin(IBootstrap bootstrap) {
        super(bootstrap.getBridge().getEnvironment());

        this.allowPercentage = true;
        this.bootstrap = bootstrap;
        this.networkClient = new NetworkClient(bootstrap);
        this.pubSubManager = new SimplePubSubManager(networkClient);

        this.setPoloBridge(bootstrap.getBridge());

    }

    /**
     * Enables this {@link CloudPlugin} instance
     * and tries to connect to the CloudMaster
     *
     * Checks if the client is already connected
     * to prevent the plugin from double-connecting
     */
    public void connectToCloud() {

        //Plugin has not been loaded before
        if (!this.networkClient.isConnected()) {
            if (getType() == PoloType.PLUGIN_PROXY) {
                PoloCloudAPI.getInstance().getCommandManager().setFilter(null);
                PoloCloudAPI.getInstance().getCommandManager().registerCommand(new CloudCommand());
            }

            this.registerSelfDestructivePacketHandler(GlobalCachePacket.class, packet -> {

                PoloCloudAPI.getInstance().updateCache(packet.getMasterCache());

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
            });

            this.networkClient.connect(nettyClient -> {
                System.out.println("[CloudPlugin] " + getName() + " successfully connected to CloudSystem! (" + nettyClient.getConnectedAddress() + ")");

                IResponse handshake =
                    PacketMessenger
                        .create()
                        .blocking()
                        .timeOutAfter(TimeUnit.SECONDS, 4L)
                        .target(PoloType.MASTER)
                        .orElse(new Response("handshake", "[CloudPlugin]Something went wrong!:false"))
                        .send("server-handshake", new JsonData("name", getName()));

                boolean hs = Boolean.parseBoolean(handshake.get("handshake").getAsString().split(":")[1]);
                String msg = handshake.get("handshake").getAsString().split(":")[0];

                if (!hs) {
                    System.out.println("=====================");
                    System.out.println("[CloudPlugin] Couldn't get Authentication-Response within 4 Seconds...");
                    System.out.println("[CloudPlugin] This does not have to be bad! Something bad could have happened while reading or writing the HandShake-Request!");
                    System.out.println("[CloudPlugin] But if things start to not work and errors occur, then this is the reason why most of the time!");
                    System.out.println("[CloudPlugin] You should report this error anyways because it should not appear");
                    System.out.println("=====================");
                } else {
                    System.out.println(msg);
                }
                Scheduler.runtimeScheduler().schedule(() -> {
                    setReloading(false);
                }, 100L);

            });

        } else {
            //Just a reload
            System.out.println("[CloudPlugin] Recognized CloudPlugin#Enable as Reload and not as Server initialisation!");
        }
    }

    @Override
    public void messageCloud(String message) {
        sendPacket(new TextPacket(message));
    }

    @Override
    public void receivePacket(Packet packet) {
        networkClient.getProtocol().firePacketHandlers(networkClient.ctx(), packet);
    }

    @Override
    public void reload() {
        this.updateCache();
    }

    /**
     * Gets the {@link JsonData} where all data about this
     * server is stored before having received the {@link de.polocloud.api.network.packets.api.MasterCache}
     */
    public JsonData getJson() {
        try {
            return new JsonData(new File(FileConstants.CLOUD_JSON_NAME));
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonData();
        }
    }

    public void setReloading(boolean reloading) {
        JsonData json = getJson();
        json.append("reloading", reloading);
        json.save();
    }

    public boolean isReloading() {
        return getJson().getBoolean("reloading");
    }

    @Override
    public boolean terminate() {
        this.loggerFactory.shutdown();
        return true;
    }

    /**
     * The {@link CloudPlugin} instance
     * to access this plugin instance
     */
    public static CloudPlugin getInstance() {
        if (PoloCloudAPI.getInstance() == null) {
            return null;
        }
        return (CloudPlugin) PoloCloudAPI.getInstance();
    }

    @Override
    public ConsoleReader getConsoleReader() {
        return null;
    }

    @Override
    public INetworkConnection getConnection() {
        return this.networkClient;
    }

    @Override
    public void reportException(Throwable throwable) {
        sendPacket(new MasterReportExceptionPacket(throwable));
    }

    @Override
    public void updateCache() {
        sendPacket(new CacheRequestPacket());
    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return new SimpleConsoleExecutor();
    }

    @Override
    public String getName() {
        return getJson().getString("GameServer-Name");
    }
}
