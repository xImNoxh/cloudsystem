package de.polocloud.modules.permission.global.api;

import de.polocloud.api.common.INamable;

import java.util.List;
import java.util.UUID;

public interface IPermissionUser extends INamable {

    /**
     * The uuid of this player
     */
    UUID getUniqueId();

    /**
     * Gets a list of all {@link IPermissionGroup}s this player has
     */
    List<IPermissionGroup> getPermissionGroups();

    /**
     * Gets a list of extra {@link IPermission}s
     * only this user has
     */
    List<IPermission> getExclusivePermissions();

    /**
     * Checks if this user has a given permission
     *
     * @param permission the permission
     * @return boolean
     */
    boolean hasPermission(String permission);

    /**
     * The {@link IPermissionGroup} with the highest id
     */
    IPermissionGroup getHighestPermissionGroup();

}
