package de.polocloud.internalwrapper;


import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.client.SimpleNettyClient;
import de.polocloud.api.network.packets.wrapper.WrapperLoginPacket;
import de.polocloud.api.network.protocol.SimpleProtocol;
import de.polocloud.internalwrapper.bootstrap.InternalWrapperBootstrap;
import de.polocloud.internalwrapper.bootstrap.WrapperBootstrap;
import de.polocloud.internalwrapper.impl.commands.ScreenCommand;
import de.polocloud.internalwrapper.impl.handler.*;
import de.polocloud.internalwrapper.impl.manager.screen.IScreen;
import de.polocloud.internalwrapper.impl.manager.screen.IScreenManager;
import de.polocloud.internalwrapper.impl.manager.screen.impl.SimpleCachedScreenManager;
import de.polocloud.internalwrapper.utils.config.WrapperConfig;

public class InternalWrapper {


    /**
     * The instance of this wrapper
     */
    private static InternalWrapper instance;

    /**
     * The connection
     */
    private SimpleNettyClient nettyClient;

    /**
     * The {@link IScreenManager} to manage all server outputs
     */
    private IScreenManager screenManager;

    /**
     * The config
     */
    private WrapperConfig wrapperConfig;

    /**
     * Bootstrap instance
     */
    private final WrapperBootstrap wrapperBootstrap;

    public InternalWrapper(WrapperBootstrap wrapperBootstrap) {
        instance = this;
        this.wrapperBootstrap = wrapperBootstrap;
    }

    /**
     * Loads the wrapper (config, screens, netty)
     */
    public void load() {

        InternalWrapperBootstrap bootstrap = new InternalWrapperBootstrap();
        this.screenManager = new SimpleCachedScreenManager();
        this.wrapperConfig = bootstrap.loadWrapperConfig(wrapperBootstrap);
        this.nettyClient = new SimpleNettyClient("127.0.0.1", PoloCloudAPI.getInstance().getMasterConfig().getProperties().getPort(), new SimpleProtocol());

        //Loading wrapper boot
        bootstrap.checkAndDeleteTmpFolder();

        //Commands
        PoloCloudAPI.getInstance().getCommandManager().registerCommand(new ScreenCommand());
    }

    /**
     * Shuts down all screens and unregisters the command
     */
    public void shutdown() {

        for (IScreen screen : screenManager.getScreens()) {
            Process process = screen.getProcess();
            if (process != null) {
                process.destroy();
            }
        }

        this.nettyClient.terminate();
        PoloCloudAPI.getInstance().getCommandManager().unregisterCommand(ScreenCommand.class);
    }

    /**
     * Connects internally to the master
     * to receive packets and handle packets
     */
    public void connect() {

        new Thread(() -> this.nettyClient.start(nettyClient -> {

            //Registering handlers
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerLoginResponse());
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerMasterRequestStart());
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerCopyServer());
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerShutdownRequest());
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerMasterRequestTerminate());

            //Logging in
            nettyClient.sendPacket(new WrapperLoginPacket(wrapperConfig.getWrapperName(), PoloCloudAPI.getInstance().getMasterConfig().getProperties().getWrapperKey()));
        })).start();
    }

    public WrapperConfig getWrapperConfig() {
        return wrapperConfig;
    }

    public IScreenManager getScreenManager() {
        return screenManager;
    }

    public static InternalWrapper getInstance() {
        return instance;
    }
}
