package de.polocloud.tablist.config;

import com.google.common.collect.Lists;
import de.polocloud.api.config.IConfig;

import java.util.List;

public class TablistConfig implements IConfig {

    private boolean activeModule = true;
    private boolean updateOnPlayerConnection = true;
    private TabInterval tabInterval = new TabInterval();

    private List<Tab> tabs = Lists.newArrayList(new Tab(new String[]{"a"}, new String[]{"casdwd"}));

    public List<Tab> getTabs() {
        return tabs;
    }


    public boolean isActiveModule() {
        return activeModule;
    }

    public boolean isUpdateOnPlayerConnection() {
        return updateOnPlayerConnection;
    }

    public TabInterval getTabInterval() {
        return tabInterval;
    }
}
