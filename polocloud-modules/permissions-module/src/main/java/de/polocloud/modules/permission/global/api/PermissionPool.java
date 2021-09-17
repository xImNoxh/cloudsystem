package de.polocloud.modules.permission.global.api;

import de.polocloud.api.inject.PoloInject;

import java.util.List;
import java.util.UUID;

public interface PermissionPool {

    static PermissionPool getInstance() {
        return PoloInject.getInstance(PermissionPool.class);
    }

    /**
     * Creates a new {@link IPermissionUser} for the database
     *
     * @param permissionUser the user
     */
    void createPermissionUser(IPermissionUser permissionUser);

    /**
     * Deletes an existing {@link IPermissionUser} from the database
     *
     * @param permissionUser the user
     */
    void deletePermissionUser(IPermissionUser permissionUser);

    /**
     * Updates an existing {@link IPermissionUser} in the database
     *
     * @param permissionUser the user
     */
    void updatePermissionUser(IPermissionUser permissionUser);

    /**
     * Creates a new {@link IPermissionGroup}
     *
     * @param permissionGroup the group
     */
    void createPermissionGroup(IPermissionGroup permissionGroup);

    /**
     * Deletes an existing {@link IPermissionGroup}
     *
     * @param permissionGroup the group
     */
    void deletePermissionGroup(IPermissionGroup permissionGroup);

    /**
     * Updates this {@link PermissionPool} instance
     */
    void update();

    /**
     * Iterates through all the permissions of a player
     * And also checks if some {@link IPermissionGroup}s or temporary {@link IPermission}s are expired
     * and need to be removed from the {@link IPermissionUser}
     *
     * @param uniqueId the uuid of the player
     */
    List<String> loadPermissions(UUID uniqueId);

    /**
     * Gets a list of all {@link IPermissionGroup}
     */
    List<IPermissionGroup> getAllCachedPermissionGroups();

    /**
     * Gets a cached {@link IPermissionGroup} by its name
     *
     * @param name the name
     * @return group or null if not cached
     */
    IPermissionGroup getCachedPermissionGroup(String name);

    /**
     * Gets a cached {@link IPermissionUser} by its uuid
     *
     * @param uniqueId the uuid
     * @return user
     */
    IPermissionUser getCachedPermissionUser(UUID uniqueId);

    /**
     * Gets a cached {@link IPermissionUser} by its name
     *
     * @param name the name
     * @return user
     */
    IPermissionUser getCachedPermissionUser(String name);

    /**
     * Gets a list of all cached {@link IPermissionUser}s
     */
    List<IPermissionUser> getAllCachedPermissionUser();

}
