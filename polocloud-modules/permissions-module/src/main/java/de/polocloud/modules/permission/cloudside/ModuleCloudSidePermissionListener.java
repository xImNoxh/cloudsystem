package de.polocloud.modules.permission.cloudside;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.player.CloudPlayerPermissionCheckEvent;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.modules.permission.global.api.IPermissionUser;
import de.polocloud.modules.permission.global.api.PermissionPool;

public class ModuleCloudSidePermissionListener implements IListener {

    @EventHandler
    public void handle(CloudPlayerPermissionCheckEvent event) {
        String permission = event.getPermission();
        ICloudPlayer player = event.getPlayer();

        IPermissionUser cachedPermissionUser = PermissionPool.getInstance().getCachedPermissionUser(player.getUUID());

        if (cachedPermissionUser == null) {
            event.setHasPermission(false);
        } else {
            event.setHasPermission(cachedPermissionUser.hasPermission(permission));
        }
    }
}
