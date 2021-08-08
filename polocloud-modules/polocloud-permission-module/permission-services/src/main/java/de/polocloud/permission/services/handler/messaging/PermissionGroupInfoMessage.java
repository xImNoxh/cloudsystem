package de.polocloud.permission.services.handler.messaging;

import de.polocloud.permission.api.permission.Permission;

import java.util.Set;

public class PermissionGroupInfoMessage {

    private final String name;
    private final Set<Permission> permissions;
    private final Set<String> groups;

    public PermissionGroupInfoMessage(String name, Set<Permission> permissions, Set<String> groups) {
        this.name = name;
        this.permissions = permissions;
        this.groups = groups;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public String getName() {
        return name;
    }


}
