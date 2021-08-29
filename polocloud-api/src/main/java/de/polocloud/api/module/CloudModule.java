package de.polocloud.api.module;

import com.google.inject.Inject;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.ITemplateManager;

public abstract class CloudModule {

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private ITemplateManager templateService;

    @Inject
    private ICloudPlayerManager playerManager;

    @Inject
    private IConfigLoader configLoader;

    @Inject
    private IConfigSaver configSaver;

    /**
     * Called when the Module is being loaded
     */
    public abstract void onLoad();

    /**
     * Called when the module is being stopped
     */
    public abstract void onShutdown();

    /**
     * Checks if the module can be reloaded
     */
    public abstract boolean canReload();

    public abstract boolean copyOnService(ITemplate... templates);

    public ITemplateManager getTemplateService() {
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
