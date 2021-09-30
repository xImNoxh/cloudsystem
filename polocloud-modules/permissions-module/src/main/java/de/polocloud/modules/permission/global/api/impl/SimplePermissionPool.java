package de.polocloud.modules.permission.global.api.impl;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.util.other.Task;
import de.polocloud.modules.permission.PermsModule;
import de.polocloud.modules.permission.global.api.IPermission;
import de.polocloud.modules.permission.global.api.IPermissionGroup;
import de.polocloud.modules.permission.global.api.IPermissionUser;
import de.polocloud.modules.permission.global.api.PermissionPool;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimplePermissionPool implements PermissionPool {

    private final CopyOnWriteArrayList<SimplePermissionGroup> permissionGroups;
    private final CopyOnWriteArrayList<SimplePermissionUser> permissionUsers;

    public SimplePermissionPool() {
        this.permissionGroups = new CopyOnWriteArrayList<>();
        this.permissionUsers = new CopyOnWriteArrayList<>();

        this.loadPoolFromCache();
    }


    @Override
    public void createPermissionUser(IPermissionUser permissionUser) {

        //Create in cache
        if (this.getCachedPermissionUser(permissionUser.getUniqueId()) == null) {
            this.permissionUsers.add((SimplePermissionUser) permissionUser);
        }

        if (!PoloCloudAPI.getInstance().getType().isPlugin()) {

            //Updating in database
            PermsModule.getInstance().getUserDatabase().insert(permissionUser.getUniqueId().toString(), (SimplePermissionUser) permissionUser);
        }
    }

    @Override
    public void deletePermissionUser(IPermissionUser permissionUser) {
        //Updating in cache
        this.permissionUsers.removeIf(simplePermissionUser -> simplePermissionUser.getName().equalsIgnoreCase(permissionUser.getName()));
        if (!PoloCloudAPI.getInstance().getType().isPlugin()) {

            //Updating in database
            PermsModule.getInstance().getUserDatabase().delete(permissionUser.getUniqueId().toString());
        }
    }

    @Override
    public void updatePermissionUser(IPermissionUser permissionUser) {
        //Updating in cache
        this.permissionUsers.removeIf(simplePermissionUser -> simplePermissionUser.getName().equalsIgnoreCase(permissionUser.getName()));
        this.permissionUsers.add((SimplePermissionUser) permissionUser);

        if (!PoloCloudAPI.getInstance().getType().isPlugin()) {

            //Updating in database
            PermsModule.getInstance().getUserDatabase().insert(permissionUser.getUniqueId().toString(), (SimplePermissionUser) permissionUser);
        }
    }

    @Override
    public void createPermissionGroup(IPermissionGroup permissionGroup) {
        if (!PoloCloudAPI.getInstance().getType().isPlugin()) {
            PermsModule.getInstance().getGroupDatabase().insert(permissionGroup.getName(), (SimplePermissionGroup) permissionGroup);
            loadPoolFromCache();
            return;
        }
        this.permissionGroups.add((SimplePermissionGroup) permissionGroup);
    }

    @Override
    public void deletePermissionGroup(IPermissionGroup permissionGroup) {
        if (!PoloCloudAPI.getInstance().getType().isPlugin()) {
            PermsModule.getInstance().getGroupDatabase().delete(permissionGroup.getName());
            loadPoolFromCache();
            return;
        }
        this.permissionGroups.removeIf(simplePermissionGroup -> simplePermissionGroup.getName().equalsIgnoreCase(permissionGroup.getName()));
    }

    private void loadPoolFromCache() {
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {

            this.permissionGroups.clear();
            this.permissionUsers.clear();

            this.permissionUsers.addAll(PermsModule.getInstance().getUserDatabase().getEntries());
            this.permissionGroups.addAll(PermsModule.getInstance().getGroupDatabase().getEntries());
        }
    }

    @Override
    public void update() {
        PermsModule.getInstance().getMessageChannel().sendMessage(new Task(PermsModule.TASK_NAME_UPDATE_POOL, new JsonData("pool", this)));
    }

    @Override
    public List<String> loadPermissions(UUID uniqueId) {
        List<String> permissions = new ArrayList<>();
        IPermissionUser permissionUser = this.getCachedPermissionUser(uniqueId);

        if (permissionUser == null) {
            return permissions;
        }
        boolean changedSomething = false;

        //Safely iterating through all groups

        for (IPermissionGroup group : permissionUser.getExpiringPermissionGroups().keySet()) {
            Long expirationDate = permissionUser.getExpiringPermissionGroups().get(group);
            if (group.hasExpired(expirationDate)) {
                changedSomething = true;
                permissionUser.removePermissionGroup(group);
            }
        }
        for (IPermission exclusivePermission : permissionUser.getExclusivePermissions()) {
            if (exclusivePermission.isTemporary() && exclusivePermission.hasExpired(exclusivePermission.getExpiringTime())) {
                changedSomething = true;
                permissionUser.removePermission(exclusivePermission.getPermission());
            }
        }

        //If a rank or a permission has been removed (something changed) we have to update to make all changes sync over the network
        if (changedSomething) {
            permissionUser.update();
        }

        //All inheritances
        for (IPermissionGroup group : permissionUser.getPermissionGroups()) {
            permissions.addAll(group.getPermissions());
            for (IPermissionGroup inheritance : group.getInheritances()) {
                permissions.addAll(inheritance.getPermissions());
            }
        }

        for (IPermission exclusivePermission : permissionUser.getExclusivePermissions()) {
            permissions.add(exclusivePermission.getPermission());
        }

        return permissions;
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
    public IPermissionUser getCachedPermissionUser(UUID uniqueId) {
        return permissionUsers.stream().filter(user -> user.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    @Override
    public IPermissionUser getCachedPermissionUser(String name) {
        return permissionUsers.stream().filter(user -> user.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public List<IPermissionUser> getAllCachedPermissionUser() {
        return new ArrayList<>(permissionUsers);
    }
}
