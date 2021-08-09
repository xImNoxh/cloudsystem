package de.polocloud.api;

import com.google.inject.Injector;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.commands.ICommandPool;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.event.IEventHandler;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.template.ITemplateService;
import org.jetbrains.annotations.NotNull;

public abstract class PoloCloudAPI {

    public static PoloCloudAPI instance;

    public static PoloCloudAPI getInstance() {
        return PoloCloudAPI.instance;
    }

    protected static void setInstance(@NotNull PoloCloudAPI instance) {
        PoloCloudAPI.instance = instance;
    }

    public abstract ITemplateService getTemplateService();

    public abstract ICommandExecutor getCommandExecutor();

    public abstract ICommandPool getCommandPool();

    public abstract IGameServerManager getGameServerManager();

    public abstract ICloudPlayerManager getCloudPlayerManager();

    public abstract IConfigLoader getConfigLoader();

    public abstract IConfigSaver getConfigSaver();

    public abstract IPubSubManager getPubSubManager();

    public abstract IProtocol getCloudProtocol();

    public abstract IEventHandler getEventHandler();

    public abstract Injector getGuice();

}
