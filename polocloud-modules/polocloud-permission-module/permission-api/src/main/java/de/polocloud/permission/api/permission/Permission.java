package de.polocloud.permission.api.permission;

public class Permission {

    private final String permissionString;
    private final long expireTime;

    public Permission(String permissionString, long expireTime) {
        this.permissionString = permissionString;
        this.expireTime = expireTime;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expireTime && expireTime != -1L;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public String getPermissionString() {
        return permissionString;
    }


}
