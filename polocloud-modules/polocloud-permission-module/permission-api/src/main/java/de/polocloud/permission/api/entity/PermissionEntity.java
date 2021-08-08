package de.polocloud.permission.api.entity;

import de.polocloud.permission.api.permission.Permission;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PermissionEntity implements IPermissionEntity {

    private final Set<Permission> permissions;

    public PermissionEntity() {
        this.permissions = new HashSet<>();
    }

    @Override
    public boolean hasPermission(String permission) {
        if (permission.isEmpty()) return true;
        if (hasAllRights()) return true;
        return getAllNotExpiredPermissions().stream().anyMatch(perm -> perm.getPermissionString().equals(permission));
    }

    @Override
    public boolean hasAllRights() {
        return getAllNotExpiredPermissions().stream().anyMatch(perm -> perm.getPermissionString().equals("*"));
    }

    @Override
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    @Override
    public void removePermission(String permission) {
        this.permissions.removeIf(perm -> perm.getPermissionString().equals(permission));
    }

    @Override
    public void clearPermissions() {
        this.permissions.clear();
    }

    @Override
    public Permission getPermissionByName(String permission) {
        return this.permissions.stream().filter(perm -> perm.getPermissionString().equals(permission)).findFirst().orElse(null);
    }

    @Override
    public Set<Permission> getPermissions() {
        return this.permissions;
    }

    @Override
    public Set<Permission> getAllNotExpiredPermissions() {
        return permissions.stream().filter(perm -> !perm.isExpired()).collect(Collectors.toSet());
    }


}
