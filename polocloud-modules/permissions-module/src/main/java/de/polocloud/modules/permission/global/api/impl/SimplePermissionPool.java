package de.polocloud.modules.permission.global.api.impl;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.util.Task;
import de.polocloud.modules.permission.PermissionModule;
import de.polocloud.modules.permission.global.api.IPermission;
import de.polocloud.modules.permission.global.api.IPermissionGroup;
import de.polocloud.modules.permission.global.api.IPermissionUser;
import de.polocloud.modules.permission.global.api.PermissionPool;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
        this.permissionGroups.add((SimplePermissionGroup) permissionGroup);
    }

    @Override
    public void deletePermissionGroup(IPermissionGroup permissionGroup) {
        if (!PoloCloudAPI.getInstance().getType().isPlugin()) {
            PermissionModule.getInstance().getGroupDatabase().delete(permissionGroup.getName());
            loadPoolFromCache();
            return;
        }
        this.permissionGroups.removeIf(simplePermissionGroup -> simplePermissionGroup.getName().equalsIgnoreCase(permissionGroup.getName()));
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
    public void update() {
        PermissionModule.getInstance().getMessageChannel().sendMessage(this);
    }

    @Override
    public void updatePermissions(UUID uniqueId,Consumer<String> accept) {
        IPermissionUser permissionUser = this.getCachedPermissionUser(uniqueId);

        if (permissionUser == null) {
            return;
        }
        AtomicBoolean changedSomething = new AtomicBoolean(false);

        //Safely iterating through all groups

        permissionUser.getExpiringPermissionGroups().forEach((group, date) -> {
            if (!group.isStillValid(uniqueId, date)) {
                changedSomething.set(true);
                permissionUser.removePermissionGroup(group);
            }
        });

        for (IPermission exclusivePermission : permissionUser.getExclusivePermissions()) {
            if (!exclusivePermission.isStillValid(uniqueId, exclusivePermission.getExpiringTime())) {
                changedSomething.set(true);
                permissionUser.removePermission(exclusivePermission.getPermission());
            }
        }

        //If a rank has been removed (something changed) we have to update to make all changes sync over the network
        if (changedSomething.get()) {
            this.update(); //Updating the whole pool
        }

        List<String> permissions = new LinkedList<>();

        //All inheritances
        for (IPermissionGroup group : permissionUser.getPermissionGroups()) {
            permissions.addAll(group.getPermissions());
            group.getInheritances().forEach(i -> permissions.addAll(i.getPermissions()));
        }

        for (IPermission exclusivePermission : permissionUser.getExclusivePermissions()) {
            permissions.add(exclusivePermission.getPermission());
        }

        //Adding all permissions that he has exclusively
        permissions.forEach(accept); //Accepting the consumer for all permissions
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
