package de.polocloud.permission.api.group;

import java.util.HashSet;
import java.util.Set;

public class PermissionGroup implements IPermissionGroup {

    private final String name;
    private int priority;
    private final Set<IPermissionGroup> inheritedPermissionGroups;

    public PermissionGroup(String name, int priority) {
        this.name = name;
        this.priority = priority;
        this.inheritedPermissionGroups = new HashSet<>();
    }

    @Override
    public void addInheritedPermissionGroup(IPermissionGroup permissionGroup) {
        this.inheritedPermissionGroups.add(permissionGroup);
    }

    @Override
    public void removeInheritedPermissionGroup(String permissionGroup) {
        this.inheritedPermissionGroups.removeIf(group -> group.getName().equals(permissionGroup));
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public Set<IPermissionGroup> getAllInheritedPermissionGroups() {
        return this.inheritedPermissionGroups;
    }

    @Override
    public void clearInheritedPermissionGroups() {
        this.inheritedPermissionGroups.clear();
    }


}
