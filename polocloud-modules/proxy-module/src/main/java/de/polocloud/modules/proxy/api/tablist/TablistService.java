package de.polocloud.modules.proxy.api.tablist;

import com.google.common.collect.Lists;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.common.IReloadable;
import de.polocloud.modules.proxy.ProxyModule;

public class TablistService implements IReloadable {

    /**
     * Updates the {@link ProxyTabList} for every online {@link ICloudPlayer}
     */
    public void updateTabList(){
        ProxyTabList tablistConfig = ProxyModule.getProxyModule().getProxyConfig().getTabList();

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

    @Override
    public void reload() {
        updateTabList();
    }
}
