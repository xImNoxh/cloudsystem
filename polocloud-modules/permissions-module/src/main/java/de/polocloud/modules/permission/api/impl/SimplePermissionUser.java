package de.polocloud.modules.permission.api.impl;

import de.polocloud.modules.permission.api.IPermission;
import de.polocloud.modules.permission.api.IPermissionGroup;
import de.polocloud.modules.permission.api.IPermissionUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@Getter @AllArgsConstructor
public class SimplePermissionUser implements IPermissionUser {

    private final String name;
    private final UUID uniqueId;
    private final List<SimplePermission> exclusivePermissions;
    private final List<String> permissionGroups;

    @Override
    public List<IPermissionGroup> getPermissionGroups() {
        return null;
    }

    @Override
    public List<IPermission> getExclusivePermissions() {
        return new ArrayList<>(exclusivePermissions);
    }

    @Override
    public boolean hasPermission(String permission) {
        boolean hasPermission = false;

        //Not null go on checking
        if (this.getExclusivePermissions().stream().anyMatch(iPermission -> iPermission.equals(permission)) || this.getExclusivePermissions().stream().anyMatch(iPermission -> iPermission.equals("*"))) {
            hasPermission = true;
        }

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
    public IPermissionGroup getHighestPermissionGroup() {
        return this.getPermissionGroups().stream().max(Comparator.comparingInt(IPermissionGroup::getId)).orElse(null);
    }
}
