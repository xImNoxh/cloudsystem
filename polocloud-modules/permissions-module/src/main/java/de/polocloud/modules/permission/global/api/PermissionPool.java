package de.polocloud.modules.permission.global.api;

import de.polocloud.api.guice.own.Guice;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface PermissionPool {

    static PermissionPool getInstance() {
        return Guice.getInstance(PermissionPool.class);
    }

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
     * @param accept consumer that accepts all the found permissions
     */
    void updatePermissions(UUID uniqueId, Consumer<String> accept);

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
     * Gets a list of all cached {@link IPermissionUser}s
     */
    List<IPermissionUser> getAllCachedPermissionUser();

}
