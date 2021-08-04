package de.polocloud.notify;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.gameserver.CloudGameServerStatusChangeEvent;
import de.polocloud.api.module.Module;
import de.polocloud.notify.collectives.CloudCollectivesListener;
import de.polocloud.notify.config.NotifyConfig;

import java.io.File;

public class NotifyModule {

    private static NotifyModule instance;

    private NotifyConfig notifyConfig;

    public NotifyModule(Module module) {
        instance = this;
        notifyConfig = loadNotifyConfig(module);

        if (notifyConfig.isUse())
            EventRegistry.registerModuleListener(module, PoloCloudAPI.getInstance().getGuice().getInstance(CloudCollectivesListener.class), CloudGameServerStatusChangeEvent.class);
    }

    public static NotifyModule getInstance() {
        return instance;
    }

    public NotifyConfig loadNotifyConfig(Module module) {
        File configPath = new File("modules/notify-system/");
        if (!configPath.exists()) configPath.mkdirs();
        File configFile = new File("modules/notify-system/config.json");
        NotifyConfig masterConfig = module.getConfigLoader().load(NotifyConfig.class, configFile);
        module.getConfigSaver().save(masterConfig, configFile);
        return masterConfig;
    }

    public NotifyConfig getNotifyConfig() {
        return notifyConfig;
    }
}
