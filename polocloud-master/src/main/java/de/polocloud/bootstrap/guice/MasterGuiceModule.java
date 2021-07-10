package de.polocloud.bootstrap.guice;

import com.google.inject.AbstractModule;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.config.MasterConfig;

public class MasterGuiceModule extends AbstractModule {

    private Master master;
    private IWrapperClientManager wrapperClientManager;
    private IGameServerManager gameServerManager;
    private ITemplateService templateService;
    private MasterConfig masterConfig;

    private IConfigLoader configLoader = new SimpleConfigLoader();
    private IConfigSaver configSaver = new SimpleConfigSaver();

    public MasterGuiceModule(MasterConfig masterConfig, Master master, IWrapperClientManager wrapperClientManager, IGameServerManager gameServerManager, ITemplateService templateService) {
        this.masterConfig = masterConfig;
        this.master = master;
        this.wrapperClientManager = wrapperClientManager;
        this.gameServerManager = gameServerManager;
        this.templateService = templateService;
        this.masterConfig = new MasterConfig();
    }


    @Override
    protected void configure() {
        bind(IGameServerManager.class).toInstance(this.gameServerManager);
        bind(ITemplateService.class).toInstance(this.templateService);
        bind(IWrapperClientManager.class).toInstance(this.wrapperClientManager);
        bind(Master.class).toInstance(this.master);

        bind(MasterConfig.class).toInstance(this.masterConfig);

        bind(IConfigLoader.class).toInstance(this.configLoader);
        bind(IConfigSaver.class).toInstance(this.configSaver);

    }
}
