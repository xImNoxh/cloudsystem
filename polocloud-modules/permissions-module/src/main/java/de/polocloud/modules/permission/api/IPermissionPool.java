package de.polocloud.modules.permission.api;

import java.util.List;

public interface IPermissionPool {

    /**
     * Gets a list of all {@link IPermissionGroup}
     */
    List<IPermissionGroup> getAllCachedPermissionGroups();

    /**
     * Gets a list of all cached {@link IPermissionUser}s
     */
    List<IPermissionUser> getAllCachedPermissionUser();

}
