package de.polocloud.tablist.config;

import com.google.common.collect.Lists;
import de.polocloud.api.config.IConfig;
import de.polocloud.tablist.config.symbol.TabSymbol;

import java.util.List;

public class TablistConfig implements IConfig {

    private boolean activeModule = true;
    private boolean updateOnPlayerConnection = true;
    private TabInterval tabInterval = new TabInterval();

    private List<Tab> tabs = Lists.newArrayList(new Tab(new TabSymbol[]{(new TabSymbol(
        "\n §8     §b§lPoloCloud§8, §7a simple Minecraft §bCloud \n §7Players §8» §b%ONLINE_COUNT%§8/§b%MAX_PLAYERS% \n",
        "\n §7  Problems or support §8» §bdc.polocloud.de \n §7Author §8» §bHttpMaco§8, §bMax_DE §7  \n"))}, new String[]{}));

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
