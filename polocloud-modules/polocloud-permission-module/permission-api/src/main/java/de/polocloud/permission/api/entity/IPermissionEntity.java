package de.polocloud.permission.api.entity;

import de.polocloud.permission.api.permission.Permission;

import java.util.Set;

public interface IPermissionEntity {

    boolean hasPermission(String permission);

    boolean hasAllRights();

    void addPermission(Permission permission);

    void removePermission(String permission);

    void clearPermissions();

    Permission getPermissionByName(String permission);

    Set<Permission> getPermissions();

    Set<Permission> getAllNotExpiredPermissions();


}
