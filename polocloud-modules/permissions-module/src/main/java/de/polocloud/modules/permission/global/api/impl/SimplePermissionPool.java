package de.polocloud.modules.permission.global.api.impl;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.util.Task;
import de.polocloud.modules.permission.PoloCloudPermissionModule;
import de.polocloud.modules.permission.global.api.IPermission;
import de.polocloud.modules.permission.global.api.IPermissionGroup;
import de.polocloud.modules.permission.global.api.IPermissionUser;
import de.polocloud.modules.permission.global.api.PermissionPool;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class SimplePermissionPool implements PermissionPool {

    private final List<SimplePermissionGroup> permissionGroups;
    private final List<SimplePermissionUser> permissionUsers;

    public SimplePermissionPool() {
        this.permissionGroups = new ArrayList<>();
        this.permissionUsers = new ArrayList<>();

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
            PoloCloudPermissionModule.getInstance().getUserDatabase().insert(permissionUser.getUniqueId().toString(), (SimplePermissionUser) permissionUser);
        }
    }

    @Override
    public void deletePermissionUser(IPermissionUser permissionUser) {
        //Updating in cache
        this.permissionUsers.removeIf(simplePermissionUser -> simplePermissionUser.getName().equalsIgnoreCase(permissionUser.getName()));
        if (!PoloCloudAPI.getInstance().getType().isPlugin()) {

            //Updating in database
            PoloCloudPermissionModule.getInstance().getUserDatabase().delete(permissionUser.getUniqueId().toString());
        }
    }

    @Override
    public void updatePermissionUser(IPermissionUser permissionUser) {
        //Updating in cache
        this.permissionUsers.removeIf(simplePermissionUser -> simplePermissionUser.getName().equalsIgnoreCase(permissionUser.getName()));
        this.permissionUsers.add((SimplePermissionUser) permissionUser);

        if (!PoloCloudAPI.getInstance().getType().isPlugin()) {

            //Updating in database
            PoloCloudPermissionModule.getInstance().getUserDatabase().insert(permissionUser.getUniqueId().toString(), (SimplePermissionUser) permissionUser);
        }
    }

    @Override
    public void createPermissionGroup(IPermissionGroup permissionGroup) {
        if (!PoloCloudAPI.getInstance().getType().isPlugin()) {
            PoloCloudPermissionModule.getInstance().getGroupDatabase().insert(permissionGroup.getName(), (SimplePermissionGroup) permissionGroup);
            loadPoolFromCache();
            return;
        }
        this.permissionGroups.add((SimplePermissionGroup) permissionGroup);
    }

    @Override
    public void deletePermissionGroup(IPermissionGroup permissionGroup) {
        if (!PoloCloudAPI.getInstance().getType().isPlugin()) {
            PoloCloudPermissionModule.getInstance().getGroupDatabase().delete(permissionGroup.getName());
            loadPoolFromCache();
            return;
        }
        this.permissionGroups.removeIf(simplePermissionGroup -> simplePermissionGroup.getName().equalsIgnoreCase(permissionGroup.getName()));
    }

    private void loadPoolFromCache() {
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {

            this.permissionGroups.clear();
            this.permissionUsers.clear();

            this.permissionUsers.addAll(PoloCloudPermissionModule.getInstance().getUserDatabase().getEntries());
            this.permissionGroups.addAll(PoloCloudPermissionModule.getInstance().getGroupDatabase().getEntries());
        }
    }

    @Override
    public void update() {
        PoloCloudPermissionModule.getInstance().getMessageChannel().sendMessage(new Task(PoloCloudPermissionModule.TASK_NAME_UPDATE_POOL, new JsonData("pool", this)));
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
            permissionUser.update();
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
