package de.polocloud.permission.api.group;

import de.polocloud.permission.api.entity.IPermissionEntity;

import java.util.Set;

public interface IPermissionGroup extends IPermissionEntity {

    void addInheritedPermissionGroup(IPermissionGroup permissionGroup);

    void removeInheritedPermissionGroup(String permissionGroup);

    void setPriority(int priority);

    String getName();

    int getPriority();

    Set<IPermissionGroup> getAllInheritedPermissionGroups();

    void clearInheritedPermissionGroups();


}
