package de.polocloud.modules.permission.global.api;

import de.polocloud.api.guice.own.Guice;

import java.util.List;

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
     * Gets a list of all cached {@link IPermissionUser}s
     */
    List<IPermissionUser> getAllCachedPermissionUser();

}
