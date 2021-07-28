package de.polocloud.tablist.executes;

import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.tablist.TablistModule;
import de.polocloud.tablist.attribute.AttributeConverter;
import de.polocloud.tablist.cache.CloudPlayerTabCache;

public class TablistUpdateExecute implements TablistExecute {

    private boolean canUpdate = true;

    @Override
    public void execute(ICloudPlayer iCloudPlayer, MasterConfig masterConfig, boolean playerUpdate) {
        if (!canUpdate) return;
        CloudPlayerTabCache cloudPlayerTabCache = TablistModule.getInstance().getCloudPlayerTabCache();
        System.out.println("update");
        cloudPlayerTabCache.keySet().forEach(key -> {
            System.out.println(key.toString());
            if (true || !key.equals(iCloudPlayer.getUUID())) {
                Master.getInstance().getCloudPlayerManager().getOnlinePlayer(key).thenAccept(player -> {
                    String[] args = AttributeConverter.convertTab(cloudPlayerTabCache.get(key).getTabs()[0].getHeader(), cloudPlayerTabCache.get(key).getTabs()[0].getFooter(), player);
                    player.sendTablist(args[0], args[1]);
                });
            }
        });
    }
}
