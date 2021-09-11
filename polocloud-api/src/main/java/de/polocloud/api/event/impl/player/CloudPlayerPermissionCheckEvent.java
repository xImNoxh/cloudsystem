package de.polocloud.api.event.impl.player;

import de.polocloud.api.player.ICloudPlayer;


public class CloudPlayerPermissionCheckEvent extends CloudPlayerEvent {

    private final String permission;

    private boolean hasPermission;

    public CloudPlayerPermissionCheckEvent(ICloudPlayer player, String permission) {
        super(player);
        this.permission = permission;
    }

    public void setHasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

    public boolean hasPermission() {
        return hasPermission;
    }

    public String getPermission() {
        return permission;
    }
}
