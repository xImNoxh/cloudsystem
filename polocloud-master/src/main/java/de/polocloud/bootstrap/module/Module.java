package de.polocloud.bootstrap.module;

import com.google.inject.Inject;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.pubsub.MasterPubSubManager;

public abstract class Module {

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private ITemplateService templateService;

    @Inject
    private ICloudPlayerManager playerManager;

    @Inject
    private MasterPubSubManager pubSubManager;

    @Inject
    private IConfigLoader configLoader;

    @Inject
    private IConfigSaver configSaver;

    public abstract void onLoad();

    public abstract void onShutdown();

    public MasterPubSubManager getPubSubManager() {
        return pubSubManager;
    }

    public ITemplateService getTemplateService() {
        return templateService;
    }

    public IConfigLoader getConfigLoader() {
        return configLoader;
    }

    public ICloudPlayerManager getPlayerManager() {
        return playerManager;
    }

    public IGameServerManager getGameServerManager() {
        return gameServerManager;
    }

    public IConfigSaver getConfigSaver() {
        return configSaver;
    }
}
