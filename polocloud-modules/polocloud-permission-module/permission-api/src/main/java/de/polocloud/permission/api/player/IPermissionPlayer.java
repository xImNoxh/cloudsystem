package de.polocloud.permission.api.player;

import de.polocloud.permission.api.group.IPermissionGroup;

import java.util.Set;
import java.util.UUID;

public interface IPermissionPlayer {

    UUID getUniqueId();

    String getName();

    void addPermissionGroup(PlayerGroupInfo info);

    void removePermissionGroup(String group);

    IPermissionGroup getHighestPermissionGroup();

    Set<IPermissionGroup> getAllPermissionGroups();

    Set<PlayerGroupInfo> getAllPlayerGroupInfos();

    Set<IPermissionGroup> getAllNotExpiredPermissionGroups();

    Set<PlayerGroupInfo> getAllNotExpiredPlayerGroupInfos();

    void clearPlayerGroupInfos();


}
