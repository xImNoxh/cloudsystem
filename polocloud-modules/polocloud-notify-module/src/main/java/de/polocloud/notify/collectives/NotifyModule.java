package de.polocloud.notify.collectives;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.gameserver.CloudGameServerStatusChangeEvent;

public class NotifyModule {

    public NotifyModule() {

        EventRegistry.registerListener(PoloCloudAPI.getInstance().getGuice().getInstance(CloudCollectivesListener.class), CloudGameServerStatusChangeEvent.class);

    }
}
