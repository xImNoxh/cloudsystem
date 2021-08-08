package de.polocloud.permission.api.player;

import de.polocloud.permission.api.group.IPermissionGroup;

public class PlayerGroupInfo {

    private final IPermissionGroup permissionGroup;
    private final long expireTime;

    public PlayerGroupInfo(IPermissionGroup permissionGroup, long expireTime) {
        this.permissionGroup = permissionGroup;
        this.expireTime = expireTime;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expireTime && expireTime != -1L;
    }

    public IPermissionGroup getPermissionGroup() {
        return permissionGroup;
    }

    public long getExpireTime() {
        return expireTime;
    }


}
