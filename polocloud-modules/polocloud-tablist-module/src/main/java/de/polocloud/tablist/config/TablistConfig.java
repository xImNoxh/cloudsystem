package de.polocloud.tablist.config;

import com.google.common.collect.Lists;
import de.polocloud.api.config.IConfig;
import de.polocloud.tablist.config.symbol.TabSymbol;

import java.util.List;

public class TablistConfig implements IConfig {

    private boolean activeModule = true;
    private boolean updateOnPlayerConnection = true;
    private TabInterval tabInterval = new TabInterval();

    private List<Tab> tabs = Lists.newArrayList(new Tab(new TabSymbol[]{(new TabSymbol("\n §b§lPolo§7Cloud §7§ov§b§o1§7.§b§o0 \n", "§8» §b§l%ONLINE_COUNT%"))}, new String[]{}));

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
