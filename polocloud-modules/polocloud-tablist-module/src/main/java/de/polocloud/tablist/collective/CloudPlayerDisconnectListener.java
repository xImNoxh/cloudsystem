package de.polocloud.tablist.collective;

import com.google.inject.Inject;
import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.tablist.TablistModule;

public class CloudPlayerDisconnectListener implements EventHandler<CloudPlayerDisconnectEvent> {


    @Inject
    private MasterConfig config;

    @Override
    public void handleEvent(CloudPlayerDisconnectEvent event) {
        ICloudPlayer player = event.getPlayer();
        TablistModule.getInstance().getTablistUpdateExecute().execute(player, config, false);

    }

}
