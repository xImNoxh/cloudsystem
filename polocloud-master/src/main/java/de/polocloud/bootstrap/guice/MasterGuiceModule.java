package de.polocloud.bootstrap.guice;

import com.google.inject.AbstractModule;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.client.IWrapperClientManager;

public class MasterGuiceModule extends AbstractModule {

    private Master master;
    private IWrapperClientManager wrapperClientManager;
    private IGameServerManager gameServerManager;
    private ITemplateService templateService;

    public MasterGuiceModule(Master master, IWrapperClientManager wrapperClientManager, IGameServerManager gameServerManager, ITemplateService templateService) {
        this.master = master;
        this.wrapperClientManager = wrapperClientManager;
        this.gameServerManager = gameServerManager;
        this.templateService = templateService;
    }


    @Override
    protected void configure() {
        bind(IGameServerManager.class).toInstance(this.gameServerManager);
        bind(ITemplateService.class).toInstance(this.templateService);
        bind(IWrapperClientManager.class).toInstance(this.wrapperClientManager);
        bind(Master.class).toInstance(this.master);
    }
}
