package de.polocloud.modules.permission.global.api;

import de.polocloud.api.common.INamable;

import java.util.List;
import java.util.Map;
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
     * Gets a {@link Map} of all {@link IPermissionGroup}s that are expiring
     * at some point and will be put into a map with the date of expiration
     */
    Map<IPermissionGroup, Long> getExpiringPermissionGroups();

    /**
     * Gets a list of extra {@link IPermission}s
     * only this user has
     */
    List<IPermission> getExclusivePermissions();

    /**
     * Gets a list of all {@link IPermission}s
     * Including the {@link IPermissionUser#getExclusivePermissions()}
     * and all the permissions of all the {@link IPermissionGroup}s
     * this user has
     */
    List<IPermission> getAllPermissions();

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

    /**
     * Adds an {@link IPermission} to this player
     * with a date when it will expire
     *
     * @param permission the permission
     * @param expiringTime the expiration date (-1 = permanent)
     */
    void addPermission(String permission, long expiringTime);

    /**
     * Removes an {@link IPermission} from this player
     *
     * @param permission the permission
     */
    void removePermission(String permission);

    /**
     * Adds an {@link IPermissionGroup} to this player
     * with a date when it will expire
     *
     * @param permissionGroup the group
     * @param expiringTime the expiration date (-1 = permanent)
     */
    void addPermissionGroup(IPermissionGroup permissionGroup, long expiringTime);

    /**
     * Removes an {@link IPermissionGroup} from this player
     *
     * @param permissionGroup the group
     */
    void removePermissionGroup(IPermissionGroup permissionGroup);

    /**
     * Updates this user and all its values
     */
    void update();
}
