package de.polocloud.modules.proxy.tablist;

import com.google.common.collect.Lists;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.modules.proxy.IProxyReload;
import de.polocloud.modules.proxy.ProxyModule;
import de.polocloud.modules.proxy.tablist.config.TablistConfig;
import de.polocloud.modules.proxy.tablist.events.CloudPlayerTablistEvents;

public class TablistService implements IProxyReload {

    private static TablistService instance;

    public TablistService() {
        instance = this;
        PoloCloudAPI.getInstance().getEventManager().registerListener(new CloudPlayerTablistEvents());
    }

    public void sendAllTablist(){
        TablistConfig tablistConfig = ProxyModule.getProxyModule().getProxyConfig().getTablistConfig();

        String header = replaceTabComponent(tablistConfig.getHeader());
        String footer = replaceTabComponent(tablistConfig.getFooter());

        for (ICloudPlayer cloudPlayer : Lists.newArrayList(PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached())) {
            cloudPlayer.sendTabList(
                header.replaceAll("%SERVICE%", (cloudPlayer.getMinecraftServer() == null ? "Logging in..." : cloudPlayer.getMinecraftServer().getName()))
                    .replaceAll("%MAX_PLAYERS%", String.valueOf(cloudPlayer.getProxyServer().getMaxPlayers())),
                footer.replaceAll("%SERVICE%", (cloudPlayer.getMinecraftServer() == null ? "Logging in..." : cloudPlayer.getMinecraftServer().getName()))
                    .replaceAll("%MAX_PLAYERS%", String.valueOf(cloudPlayer.getProxyServer().getMaxPlayers())));
        }
    }

    public String replaceTabComponent(String value){
        return value.replaceAll("%ONLINE_PLAYERS%", String.valueOf(PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached().size()));
    }

    public static TablistService getInstance() {
        return instance;
    }

    @Override
    public void onReload() {
        sendAllTablist();
    }
}
