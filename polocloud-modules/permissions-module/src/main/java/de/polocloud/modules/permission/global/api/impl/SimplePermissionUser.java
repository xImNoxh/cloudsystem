package de.polocloud.modules.permission.global.api.impl;

import de.polocloud.api.config.JsonData;
import de.polocloud.api.util.Task;
import de.polocloud.modules.permission.PermissionModule;
import de.polocloud.modules.permission.global.api.IPermission;
import de.polocloud.modules.permission.global.api.IPermissionGroup;
import de.polocloud.modules.permission.global.api.IPermissionUser;
import de.polocloud.modules.permission.global.api.PermissionPool;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter @AllArgsConstructor @Setter
public class SimplePermissionUser implements IPermissionUser {

    private String name;
    private UUID uniqueId;
    private final Map<String, Long> permissionGroups;
    private final List<SimplePermission> exclusivePermissions;

    @Override
    public List<IPermissionGroup> getPermissionGroups() {
        List<IPermissionGroup> list = new ArrayList<>();
        for (String permissionGroup : this.permissionGroups.keySet()) {
            PermissionPool.getInstance().getAllCachedPermissionGroups().stream().filter(group -> group.getName().equalsIgnoreCase(permissionGroup)).findFirst().ifPresent(list::add);
        }

        return list;
    }

    @Override
    public List<IPermission> getExclusivePermissions() {
        return new ArrayList<>(exclusivePermissions);
    }

    @Override
    public List<IPermission> getAllPermissions() {
        List<IPermission> list = new ArrayList<>(this.getExclusivePermissions());
        PermissionPool.getInstance().updatePermissions(this.uniqueId, s -> list.add(new SimplePermission(s)));
        return list;
    }

    @Override
    public boolean hasPermission(String permission) {
        boolean hasPermission = this.getExclusivePermissions().stream().anyMatch(iPermission -> iPermission.equals(permission)) || this.getExclusivePermissions().stream().anyMatch(iPermission -> iPermission.equals("*"));

        //Safely iterating through permissionGroups
        for (IPermissionGroup permissionGroup : new LinkedList<>(getPermissionGroups())) {
            //Checking if group has all rights (*) or the specific permission
            if (permissionGroup.getPermissions().stream().anyMatch(iPermission -> iPermission.equals("*")) || permissionGroup.getPermissions().stream().anyMatch(iPermission -> iPermission.equals(permission))) {
                hasPermission = true;
                break;
            }

            //Checking if inheritances have the perms
            for (IPermissionGroup inheritance : permissionGroup.getInheritances()) {
                if (inheritance.getPermissions().stream().anyMatch(iPermission -> iPermission.equals("*")) || inheritance.getPermissions().stream().anyMatch(iPermission -> iPermission.equals(permission))) {
                    hasPermission = true;
                    break;
                }
            }
        }

        //Returning value
        return hasPermission;
    }

    @Override
    public Map<IPermissionGroup, Long> getExpiringPermissionGroups() {
        Map<IPermissionGroup, Long> map = new HashMap<>();
        for (String s : this.permissionGroups.keySet()) {
            Long expiringDate = this.permissionGroups.get(s);
            IPermissionGroup permissionGroup = this.getPermissionGroups().stream().filter(permissionGroup1 -> permissionGroup1.getName().equalsIgnoreCase(s)).findFirst().orElse(null);
            if (permissionGroup != null) {
                map.put(permissionGroup, expiringDate);
            }
        }
        return map;
    }

    @Override
    public IPermissionGroup getHighestPermissionGroup() {
        return this.getPermissionGroups().stream().max(Comparator.comparingInt(IPermissionGroup::getId)).orElse(null);
    }

    @Override
    public void addPermission(String permission, long expiringTime) {
        this.exclusivePermissions.add(new SimplePermission(permission, expiringTime));
    }

    @Override
    public void removePermission(String permission) {
        this.exclusivePermissions.removeIf(simplePermission -> simplePermission.getPermission().equalsIgnoreCase(permission));
    }

    @Override
    public void addPermissionGroup(IPermissionGroup permissionGroup, long expiringTime) {
        this.permissionGroups.put(permissionGroup.getName(), expiringTime);
    }

    @Override
    public void removePermissionGroup(IPermissionGroup permissionGroup) {
        this.permissionGroups.remove(permissionGroup.getName());
    }

    @Override
    public void update() {
        PermissionModule.getInstance().getTaskChannels().sendMessage(new Task("update-user", new JsonData("user", this)));
    }
}
