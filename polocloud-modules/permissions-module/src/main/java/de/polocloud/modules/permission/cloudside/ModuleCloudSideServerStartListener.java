package de.polocloud.modules.permission.cloudside;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.server.CloudGameServerStatusChangeEvent;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.modules.permission.PoloCloudPermissionModule;

@AutoRegistry
public class ModuleCloudSideServerStartListener implements IListener {

    @EventHandler
    public void handle(CloudGameServerStatusChangeEvent event) {
        PoloCloudPermissionModule.getInstance().reload();
    }
}
