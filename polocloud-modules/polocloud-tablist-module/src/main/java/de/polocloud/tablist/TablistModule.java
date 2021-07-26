package de.polocloud.tablist;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.player.CloudPlayerJoinNetworkEvent;
import de.polocloud.api.module.Module;
import de.polocloud.tablist.collective.CollectiveCloudEvents;
import de.polocloud.tablist.config.TablistConfig;

import java.io.File;

public class TablistModule {

    private TablistConfig tablistConfig;

    public TablistModule(Module module) {
        this.tablistConfig = loadTablistConfig(module);

        if (tablistConfig.isActiveModule())
            EventRegistry.registerListener(PoloCloudAPI.getInstance().getGuice().getInstance(CollectiveCloudEvents.class), CloudPlayerJoinNetworkEvent.class);

    }

    public TablistConfig loadTablistConfig(Module module) {
        File configPath = new File("modules/tablist/");
        if (!configPath.exists()) configPath.mkdirs();

        File configFile = new File("modules/tablist/config.json");

        TablistConfig tablistConfig = module.getConfigLoader().load(TablistConfig.class, configFile);
        module.getConfigSaver().save(tablistConfig, configFile);
        return tablistConfig;
    }

    public TablistConfig getTablistConfig() {
        return tablistConfig;
    }

    public void setTablistConfig(TablistConfig tablistConfig) {
        this.tablistConfig = tablistConfig;
    }
}
