package de.polocloud.modules.permission.cloudside.listener;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.server.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.modules.permission.PermissionModule;

@AutoRegistry
public class ModuleCloudSideServerStartListener implements IListener {

    @EventHandler
    public void handle(CloudGameServerStatusChangeEvent event) {

        if (event.getStatus() == GameServerStatus.AVAILABLE) {
            PermissionModule.getInstance().reload();
        }
    }
}
