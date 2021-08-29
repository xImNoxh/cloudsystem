package de.polocloud.bootstrap.guice;

import com.google.inject.AbstractModule;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.template.ITemplateManager;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.pubsub.MasterPubSubManager;

public class MasterGuiceModule extends AbstractModule {

    private Master master;
    private IGameServerManager gameServerManager;
    private ITemplateManager templateService;
    private MasterConfig masterConfig;
    private ICloudPlayerManager cloudPlayerManager;

    private IConfigLoader configLoader = new SimpleConfigLoader();
    private IConfigSaver configSaver = new SimpleConfigSaver();

    private MasterPubSubManager pubSubManager = new MasterPubSubManager();

    public MasterGuiceModule(MasterConfig masterConfig, Master master,
                             IGameServerManager gameServerManager, ITemplateManager templateService, ICloudPlayerManager cloudPlayerManager) {
        this.masterConfig = masterConfig;
        this.master = master;
        this.gameServerManager = gameServerManager;
        this.templateService = templateService;
        this.cloudPlayerManager = cloudPlayerManager;
    }


    @Override
    protected void configure() {
        bind(ICloudPlayerManager.class).toInstance(this.cloudPlayerManager);
        bind(IGameServerManager.class).toInstance(this.gameServerManager);
        bind(ITemplateManager.class).toInstance(this.templateService);
        bind(Master.class).toInstance(this.master);

        bind(MasterPubSubManager.class).toInstance(this.pubSubManager);

        bind(MasterConfig.class).toInstance(this.masterConfig);

        bind(IConfigLoader.class).toInstance(this.configLoader);
        bind(IConfigSaver.class).toInstance(this.configSaver);

    }
}
