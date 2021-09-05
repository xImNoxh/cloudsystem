package de.polocloud.modules.permission.api;

import de.polocloud.api.guice.own.Guice;

import java.util.List;

public interface PermissionPool {

    static PermissionPool getInstance() {
        return Guice.getInstance(PermissionPool.class);
    }

    /**
     * Gets a list of all {@link IPermissionGroup}
     */
    List<IPermissionGroup> getAllCachedPermissionGroups();

    /**
     * Gets a list of all cached {@link IPermissionUser}s
     */
    List<IPermissionUser> getAllCachedPermissionUser();

}
