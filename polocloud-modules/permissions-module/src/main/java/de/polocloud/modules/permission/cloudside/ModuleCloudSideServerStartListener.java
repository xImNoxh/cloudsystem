package de.polocloud.modules.permission.cloudside;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.server.GameServerStatusChangeEvent;
import de.polocloud.api.common.AutoRegistry;
import de.polocloud.modules.permission.PermsModule;

@AutoRegistry
public class ModuleCloudSideServerStartListener implements IListener {

    @EventHandler
    public void handle(GameServerStatusChangeEvent event) {
        PermsModule.getInstance().reload();
    }
}
