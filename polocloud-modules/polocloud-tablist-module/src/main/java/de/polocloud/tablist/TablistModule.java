package de.polocloud.tablist;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.player.CloudPlayerJoinNetworkEvent;
import de.polocloud.api.event.player.CloudPlayerSwitchServerEvent;
import de.polocloud.api.module.Module;
import de.polocloud.tablist.attribute.AttributeConverter;
import de.polocloud.tablist.cache.CloudPlayerTabCache;
import de.polocloud.tablist.collective.CloudPlayerDisconnectListener;
import de.polocloud.tablist.collective.CloudPlayerJoinListener;
import de.polocloud.tablist.collective.CloudPlayerSwitchListener;
import de.polocloud.tablist.config.TablistConfig;
import de.polocloud.tablist.executes.TablistSetExecute;
import de.polocloud.tablist.executes.TablistUpdateExecute;
import de.polocloud.tablist.scheduler.IntervalRunnable;

import java.io.File;

public class TablistModule {


    private static TablistModule instance;

    private TablistConfig tablistConfig;
    private IntervalRunnable intervalRunnable;
    private CloudPlayerTabCache cloudPlayerTabCache;
    private AttributeConverter attributeConverter;

    private TablistSetExecute tablistSetExecute;
    private TablistUpdateExecute tablistUpdateExecute;


    public TablistModule(Module module) {
        instance = this;
        this.tablistConfig = loadTablistConfig(module);
        this.cloudPlayerTabCache = new CloudPlayerTabCache();
        this.tablistUpdateExecute = new TablistUpdateExecute();

        if (tablistConfig.isActiveModule()) {
            this.attributeConverter = new AttributeConverter();
            this.tablistSetExecute = new TablistSetExecute();
            EventRegistry.registerModuleListener(module, PoloCloudAPI.getInstance().getGuice().getInstance(CloudPlayerJoinListener.class), CloudPlayerJoinNetworkEvent.class);
            EventRegistry.registerModuleListener(module, PoloCloudAPI.getInstance().getGuice().getInstance(CloudPlayerDisconnectListener.class), CloudPlayerDisconnectEvent.class);
            EventRegistry.registerModuleListener(module, PoloCloudAPI.getInstance().getGuice().getInstance(CloudPlayerSwitchListener.class), CloudPlayerSwitchServerEvent.class);

            intervalRunnable = new IntervalRunnable(tablistConfig);
        }

    }

    public static TablistModule getInstance() {
        return instance;
    }

    public TablistUpdateExecute getTablistUpdateExecute() {
        return tablistUpdateExecute;
    }

    public TablistConfig loadTablistConfig(Module module) {
        File configPath = new File("modules/tablist/");
        if (!configPath.exists()) configPath.mkdirs();

        File configFile = new File("modules/tablist/config.json");

        TablistConfig tablistConfig = module.getConfigLoader().load(TablistConfig.class, configFile);
        module.getConfigSaver().save(tablistConfig, configFile);
        return tablistConfig;
    }

    public IntervalRunnable getIntervalRunnable() {
        return intervalRunnable;
    }

    public CloudPlayerTabCache getCloudPlayerTabCache() {
        return cloudPlayerTabCache;
    }

    public TablistSetExecute getTablistSetExecute() {
        return tablistSetExecute;
    }

    public TablistConfig getTablistConfig() {
        return tablistConfig;
    }

    public void setTablistConfig(TablistConfig tablistConfig) {
        this.tablistConfig = tablistConfig;
    }

    public CloudPlayerTabCache getTabCache() {
        return cloudPlayerTabCache;
    }

    public AttributeConverter getAttributeConverter() {
        return attributeConverter;
    }
}
