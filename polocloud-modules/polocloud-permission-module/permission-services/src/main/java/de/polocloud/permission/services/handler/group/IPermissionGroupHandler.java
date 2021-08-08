package de.polocloud.permission.services.handler.group;

import de.polocloud.permission.api.group.IPermissionGroup;
import de.polocloud.permission.api.permission.Permission;

import java.util.Collection;
import java.util.Set;

public interface IPermissionGroupHandler {

    Collection<IPermissionGroup> getAllPermissionGroups();

    IPermissionGroup getPermissionGroupByName(String name);

    IPermissionGroup getDefaultGroup();

    boolean existsGroup(String name);

    void save(IPermissionGroup permissionGroup);

    void sync(IPermissionGroup permissionGroup);

    void delete(IPermissionGroup permissionGroup);

    void add(IPermissionGroup permissionGroup);

    void update(IPermissionGroup permissionGroup, Set<Permission> permissions, Set<IPermissionGroup> groups);

    Collection<IPermissionGroup> sortGroupList(Collection<String> groups);


}
