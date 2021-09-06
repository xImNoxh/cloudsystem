package de.polocloud.modules.permission.global.api.impl;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.modules.permission.PermissionModule;
import de.polocloud.modules.permission.global.api.IPermissionGroup;
import de.polocloud.modules.permission.global.api.IPermissionUser;
import de.polocloud.modules.permission.global.api.PermissionPool;

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


    @Override
    public void createPermissionGroup(IPermissionGroup permissionGroup) {
        if (!PoloCloudAPI.getInstance().getType().isPlugin()) {
            PermissionModule.getInstance().getGroupDatabase().insert(permissionGroup.getName(), (SimplePermissionGroup) permissionGroup);
            loadPoolFromCache();
            return;
        }
        //TODO MINECRAFT SIDE
    }

    @Override
    public void deletePermissionGroup(IPermissionGroup permissionGroup) {
        if (!PoloCloudAPI.getInstance().getType().isPlugin()) {
            PermissionModule.getInstance().getGroupDatabase().delete(permissionGroup.getName());
            loadPoolFromCache();
            return;
        }
        //TODO MINECRAFT SIDE
    }

    private void loadPoolFromCache() {
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {

            this.permissionGroups.clear();
            this.permissionUsers.clear();

            this.permissionUsers.addAll(PermissionModule.getInstance().getUserDatabase().getEntries());
            this.permissionGroups.addAll(PermissionModule.getInstance().getGroupDatabase().getEntries());
        }
    }

    @Override
    public IPermissionGroup getCachedPermissionGroup(String name) {
        return permissionGroups.stream().filter(group -> group.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
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
