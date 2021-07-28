package de.polocloud.tablist.collective;

import com.google.inject.Inject;
import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.player.CloudPlayerSwitchServerEvent;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.tablist.TablistModule;

public class CloudPlayerSwitchListener implements EventHandler<CloudPlayerSwitchServerEvent> {

    @Inject
    private MasterConfig config;

    @Override
    public void handleEvent(CloudPlayerSwitchServerEvent event) {
        TablistModule.getInstance().getTablistUpdateExecute().execute(event.getPlayer(), config, true);
    }
}
