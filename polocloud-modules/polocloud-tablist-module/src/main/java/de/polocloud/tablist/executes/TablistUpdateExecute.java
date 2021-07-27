package de.polocloud.tablist.executes;

import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.tablist.TablistModule;
import de.polocloud.tablist.attribute.AttributeConverter;
import de.polocloud.tablist.cache.CloudPlayerTabCache;

import java.util.concurrent.ExecutionException;

public class TablistUpdateExecute implements TablistExecute{

    private boolean canUpdate = true;

    @Override
    public void execute(ICloudPlayer iCloudPlayer, MasterConfig masterConfig) {
        if(!canUpdate) return;
        CloudPlayerTabCache cloudPlayerTabCache = TablistModule.getInstance().getCloudPlayerTabCache();
        cloudPlayerTabCache.keySet().forEach(key -> {
            String[] args = AttributeConverter.convertTab(cloudPlayerTabCache.get(key).getTabs()[0].getHeader(), cloudPlayerTabCache.get(key).getTabs()[0].getFooter(), iCloudPlayer);
            try {
                Master.getInstance().getCloudPlayerManager().getOnlinePlayer(key).get().sendTablist(args[0], args[1]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
