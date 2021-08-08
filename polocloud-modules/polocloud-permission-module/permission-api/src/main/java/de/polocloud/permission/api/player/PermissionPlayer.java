package de.polocloud.permission.api.player;

import de.polocloud.permission.api.comperator.InvertedPermissionGroupComparator;
import de.polocloud.permission.api.entity.PermissionEntity;
import de.polocloud.permission.api.group.IPermissionGroup;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PermissionPlayer extends PermissionEntity implements IPermissionPlayer {

    private final UUID uuid;
    private final String name;
    private final Set<PlayerGroupInfo> permissionGroups;

    public PermissionPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.permissionGroups = new HashSet<>();
    }

    @Override
    public boolean hasPermission(String permission) {
        if (permission.isEmpty()) return true;
        if (hasAllRights()) return true;
        if (getAllNotExpiredPermissions().stream().anyMatch(perm -> perm.getPermissionString().equals(permission)))
            return true;
        for (IPermissionGroup group : getAllNotExpiredPermissionGroups())
            if (group.hasPermission(permission)) return true;
        return false;
    }

    @Override
    public IPermissionGroup getHighestPermissionGroup() {
        return getAllNotExpiredPlayerGroupInfos().stream().map(PlayerGroupInfo::getPermissionGroup).max(new InvertedPermissionGroupComparator()).get();
    }

    @Override
    public Set<IPermissionGroup> getAllPermissionGroups() {
        return permissionGroups.stream().map(PlayerGroupInfo::getPermissionGroup).collect(Collectors.toSet());
    }

    @Override
    public Set<IPermissionGroup> getAllNotExpiredPermissionGroups() {
        return getAllNotExpiredPlayerGroupInfos().stream().map(PlayerGroupInfo::getPermissionGroup).collect(Collectors.toSet());
    }

    @Override
    public Set<PlayerGroupInfo> getAllNotExpiredPlayerGroupInfos() {
        return permissionGroups.stream().filter(info -> !info.isExpired()).collect(Collectors.toSet());
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addPermissionGroup(PlayerGroupInfo info) {
        permissionGroups.add(info);
    }

    @Override
    public void removePermissionGroup(String group) {
        permissionGroups.removeIf(info -> info.getPermissionGroup().getName().equals(group));
    }

    @Override
    public void clearPlayerGroupInfos() {
        this.permissionGroups.clear();
    }

    @Override
    public Set<PlayerGroupInfo> getAllPlayerGroupInfos() {
        return permissionGroups;
    }


}
