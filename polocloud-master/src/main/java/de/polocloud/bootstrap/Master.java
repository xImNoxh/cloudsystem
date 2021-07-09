package de.polocloud.bootstrap;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.guice.PoloAPIGuiceModule;
import de.polocloud.api.network.IStartable;
import de.polocloud.api.network.ITerminatable;
import de.polocloud.api.network.server.SimpleNettyServer;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.SimpleWrapperClientManager;
import de.polocloud.bootstrap.commands.StopCommand;
import de.polocloud.bootstrap.commands.GameServerCloudCommand;
import de.polocloud.bootstrap.commands.TemplateCloudCommand;
import de.polocloud.bootstrap.creator.ServerCreatorRunner;
import de.polocloud.bootstrap.gameserver.SimpleGameServerManager;
import de.polocloud.bootstrap.guice.MasterGuiceModule;
import de.polocloud.bootstrap.network.handler.GameServerRegisterPacketHandler;
import de.polocloud.bootstrap.network.handler.WrapperLoginPacketHandler;
import de.polocloud.bootstrap.template.SimpleTemplateService;
import de.polocloud.bootstrap.template.TemplateStorage;

public class Master implements IStartable, ITerminatable {

    private final CloudAPI cloudAPI;

    private SimpleNettyServer nettyServer;

    private final ITemplateService templateService;
    private final IWrapperClientManager wrapperClientManager;
    private final IGameServerManager gameServerManager;

    public static final String LOGIN_KEY = "xXxPoloxXxCloudxXx";

    private boolean running = false;

    public Master() {

        this.wrapperClientManager = new SimpleWrapperClientManager();
        this.gameServerManager = new SimpleGameServerManager();
        this.templateService = new SimpleTemplateService();

        this.cloudAPI = new PoloCloudAPI(new PoloAPIGuiceModule(), new MasterGuiceModule(this, wrapperClientManager, this.gameServerManager, templateService));

        ((SimpleTemplateService) this.templateService).load(this.cloudAPI, TemplateStorage.FILE);
        this.templateService.getTemplateLoader().loadTemplates();

        CloudAPI.getInstance().getCommandPool().registerCommand(new StopCommand());
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new TemplateCloudCommand(this.templateService));
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new GameServerCloudCommand(this.templateService, this.wrapperClientManager));

        Thread runnerThread = new Thread(PoloCloudAPI.getInstance().getGuice().getInstance(ServerCreatorRunner.class));
        runnerThread.start();

    }

    @Override
    public void start() {
        running = true;
        this.nettyServer = this.cloudAPI.getGuice().getInstance(SimpleNettyServer.class);

        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(WrapperLoginPacketHandler.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(GameServerRegisterPacketHandler.class));


        System.out.println("starting...");
        new Thread(() -> nettyServer.start()).start();

        System.out.println("started");
    }


    @Override
    public boolean terminate() {
        this.running = false;
        return this.nettyServer.terminate();
    }

    public boolean isRunning() {
        return running;
    }
}
