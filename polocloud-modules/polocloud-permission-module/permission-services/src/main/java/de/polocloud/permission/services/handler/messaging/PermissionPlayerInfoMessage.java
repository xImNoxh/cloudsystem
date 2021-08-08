package de.polocloud.permission.services.handler.messaging;

import de.polocloud.permission.api.permission.Permission;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PermissionPlayerInfoMessage {

    private final UUID uuid;
    private final Set<Permission> permissions;
    private final Map<String, Long> permissionGroupInfos;

    public PermissionPlayerInfoMessage(UUID uuid, Set<Permission> permissions, Map<String, Long> playerGroupInfos) {
        this.uuid = uuid;
        this.permissions = permissions;
        this.permissionGroupInfos = new HashMap<>(playerGroupInfos);
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Map<String, Long> getPlayerGroupInfos() {
        return permissionGroupInfos;
    }

    public UUID getUUID() {
        return uuid;
    }

}
