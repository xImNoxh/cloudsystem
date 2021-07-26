package de.polocloud.signs.config;

import de.polocloud.api.config.IConfig;
import de.polocloud.signs.config.locations.LocationConfig;
import de.polocloud.signs.config.permissions.PermissionConfig;

public class SignConfig implements IConfig {

    private boolean use = true;

    private boolean onlyConnectIsEmpty = true;
    private boolean canIgnoreIsEmptyAccessWithPermission = true;

    private PermissionConfig permissionConfig = new PermissionConfig();
    private LocationConfig locationConfig = new LocationConfig();


    public LocationConfig getLocationConfig() {
        return locationConfig;
    }

    public boolean isUse() {
        return use;
    }

    public boolean isCanIgnoreIsEmptyAccessWithPermission() {
        return canIgnoreIsEmptyAccessWithPermission;
    }

    public void setCanIgnoreIsEmptyAccessWithPermission(boolean canIgnoreIsEmptyAccessWithPermission) {
        this.canIgnoreIsEmptyAccessWithPermission = canIgnoreIsEmptyAccessWithPermission;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    public boolean isOnlyConnectIsEmpty() {
        return onlyConnectIsEmpty;
    }

    public void setOnlyConnectIsEmpty(boolean onlyConnectIsEmpty) {
        this.onlyConnectIsEmpty = onlyConnectIsEmpty;
    }

    public PermissionConfig getPermissionConfig() {
        return permissionConfig;
    }

    public void setPermissionConfig(PermissionConfig permissionConfig) {
        this.permissionConfig = permissionConfig;
    }

    public void setLocationConfig(LocationConfig locationConfig) {
        this.locationConfig = locationConfig;
    }
}
