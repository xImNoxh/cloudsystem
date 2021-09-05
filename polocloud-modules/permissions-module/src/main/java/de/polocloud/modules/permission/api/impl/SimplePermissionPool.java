package de.polocloud.modules.permission.api.impl;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.modules.permission.PermissionModule;
import de.polocloud.modules.permission.api.IPermissionGroup;
import de.polocloud.modules.permission.api.IPermissionUser;
import de.polocloud.modules.permission.api.PermissionPool;

import java.util.ArrayList;
import java.util.List;

public class SimplePermissionPool implements PermissionPool {

    private final List<SimplePermissionGroup> permissionGroups;
    private final List<SimplePermissionUser> permissionUsers;

    public SimplePermissionPool() {
        this.permissionGroups = new ArrayList<>();
        this.permissionUsers = new ArrayList<>();

        this.loadPoolFromCache();
    }


    private void loadPoolFromCache() {
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
            this.permissionUsers.addAll(PermissionModule.getInstance().getUserDatabase().getEntries());
            this.permissionGroups.addAll(PermissionModule.getInstance().getGroupDatabase().getEntries());
        }
    }

    @Override
    public List<IPermissionGroup> getAllCachedPermissionGroups() {
        return new ArrayList<>(permissionGroups);
    }

    @Override
    public List<IPermissionUser> getAllCachedPermissionUser() {
        return new ArrayList<>(permissionUsers);
    }
}
