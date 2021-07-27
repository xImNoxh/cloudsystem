package de.polocloud.tablist.collective;

import com.google.inject.Inject;
import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.player.CloudPlayerJoinNetworkEvent;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.tablist.TablistModule;
import de.polocloud.tablist.cache.CloudPlayerTabCache;
import de.polocloud.tablist.config.Tab;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class CloudPlayerJoinListener implements EventHandler<CloudPlayerJoinNetworkEvent> {

    public boolean canUpdate;

    @Inject
    private MasterConfig config;

    public CloudPlayerJoinListener() {
        this.canUpdate = TablistModule.getInstance().getTablistConfig().isUpdateOnPlayerConnection();
    }

    @Override
    public void handleEvent(CloudPlayerJoinNetworkEvent event) {
        ICloudPlayer player = event.getPlayer();

        CloudPlayerTabCache cloudPlayerTabCache = TablistModule.getInstance().getTabCache();

        Tab tab = TablistModule.getInstance().getTablistConfig().getTabs().stream()
            .filter(key -> key.getUse() && (key.getGroups().length <= 0 || Arrays.stream(key.getGroups())
                .anyMatch(it -> it.equalsIgnoreCase(config.getProperties().getFallback()[0])))).findAny().orElse(null);

        if (tab != null) {
           cloudPlayerTabCache.put(player.getUUID(), tab);
            player.sendTablist(tab.getTabs()[0].getHeader().replace("%ONLINE_COUNT%", String.valueOf(getOnlineCount()))
                , tab.getTabs()[0].getFooter().replace("%ONLINE_COUNT%", String.valueOf(getOnlineCount())));
        }

        if(!canUpdate) return;
        cloudPlayerTabCache.keySet().forEach(key -> {
            try {
                if(!key.equals(player.getUUID()))
                Master.getInstance().getCloudPlayerManager().getOnlinePlayer(key).get().sendTablist(cloudPlayerTabCache.get(key).getTabs()[0].getHeader().replace("%ONLINE_COUNT%", String.valueOf(getOnlineCount()))
                    , cloudPlayerTabCache.get(key).getTabs()[0].getFooter().replace("%ONLINE_COUNT%", String.valueOf(getOnlineCount())));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });



    }

    public int getOnlineCount() {
        try {
            return Master.getInstance().getCloudPlayerManager().getAllOnlinePlayers().get().size();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
