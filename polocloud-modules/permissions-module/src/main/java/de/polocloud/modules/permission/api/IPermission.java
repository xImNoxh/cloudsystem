package de.polocloud.modules.permission.api;

public interface IPermission {

    /**
     * The String permission of this permission
     */
    String getPermission();

    /**
     * Checks if it equals a string permission
     *
     * @param perms the permission
     */
    boolean equals(String perms);

    /**
     * If this permission is temporary
     * and can expire at some time or its permanent
     * and will never expire
     */
    boolean isTemporary();

    /**
     * The time when this permission expires
     *
     * @return time as long
     */
    long getExpiringTime();
}
