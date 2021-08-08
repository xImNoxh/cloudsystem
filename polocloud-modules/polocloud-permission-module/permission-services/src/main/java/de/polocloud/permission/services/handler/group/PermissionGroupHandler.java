package de.polocloud.permission.services.handler.group;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import de.polocloud.permission.api.comperator.InvertedPermissionGroupComparator;
import de.polocloud.permission.api.group.IPermissionGroup;
import de.polocloud.permission.api.group.PermissionGroup;
import de.polocloud.permission.api.permission.Permission;
import de.polocloud.permission.services.Permissions;
import de.polocloud.permission.services.database.PermissionGroupSqlProvider;
import de.polocloud.permission.services.database.PermissionPlayerSqlProvider;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class PermissionGroupHandler extends PermissionGroupSqlProvider implements IPermissionGroupHandler {

    private String defaultGroup = "Spieler";
    Collection<IPermissionGroup> permissionGroups;

    public PermissionGroupHandler() {
        permissionGroups = new CopyOnWriteArrayList<>();

        createDefaultGroup();
        initGroups();
    }

    private void createDefaultGroup() {
        if(existsSqlGroup(defaultGroup)) return;

        createGroup(defaultGroup, 0);
        System.out.println("[Permissions] Created default group");
    }

    private void initGroups() {
        Futures.addCallback(getAllPermissionGroupsFromSql(), new FutureCallback<Collection<String>>() {
            @Override
            public void onSuccess(Collection<String> strings) {
                for(IPermissionGroup group : sortGroupList(strings)) {
                    for(Permission permission : getPermissions(group.getName())) {
                        group.addPermission(permission);
                    }
                    for(IPermissionGroup g : getAllInheritedGroups(group.getName())) {
                        group.addInheritedPermissionGroup(g);
                    }
                    permissionGroups.add(group);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("failed to load all permission groups");
            }
        });
        System.out.println("[Permissions] Initialized all groups");
    }

    public Collection<IPermissionGroup> getAllInheritedGroups(String name) {
        Collection<IPermissionGroup> list = new CopyOnWriteArrayList<>();
        Futures.addCallback(getInheritedGroupsString(name), new FutureCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if(s.isEmpty()) return;
                String[] groupSplit = s.split(";");
                for(String group : groupSplit) {
                    list.add(getPermissionGroupByName(group));
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("failed to load inherited groups from "+name);

            }
        });
        return list;
    }

    @Override
    public Collection<IPermissionGroup> sortGroupList(Collection<String> groups) {
        Collection<IPermissionGroup> list = new CopyOnWriteArrayList<>();
        for(String group : groups) {
            list.add(new PermissionGroup(group, getPriority(group)));
        }
        return list.stream().sorted(new InvertedPermissionGroupComparator()).collect(Collectors.toList());
    }

    @Override
    public Collection<IPermissionGroup> getAllPermissionGroups() {
        return permissionGroups;
    }

    @Override
    public IPermissionGroup getPermissionGroupByName(String name) {
        return permissionGroups.stream().filter(group -> group.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public IPermissionGroup getDefaultGroup() {
        return getPermissionGroupByName(defaultGroup);
    }

    @Override
    public void save(IPermissionGroup permissionGroup) {
        setPermissions(permissionGroup.getName(), permissionGroup.getAllNotExpiredPermissions());
        setInheritedGroups(permissionGroup.getName(), permissionGroup.getAllInheritedPermissionGroups());
        setPriority(permissionGroup.getName(), permissionGroup.getPriority());
        permissionGroups.remove(permissionGroup);
        permissionGroups.add(permissionGroup);
    }

    @Override
    public void delete(IPermissionGroup permissionGroup) {
        deleteGroup(permissionGroup.getName());
        permissionGroups.remove(permissionGroup);
    }

    @Override
    public void add(IPermissionGroup permissionGroup) {
        createGroup(permissionGroup.getName(), permissionGroup.getPriority());
        permissionGroups.add(permissionGroup);
    }

    @Override
    public void sync(IPermissionGroup permissionGroup) {
        Permissions.getInstance().getMessageHandler().sendPermissionGroupInfo(permissionGroup);
    }

    @Override
    public void update(IPermissionGroup permissionGroup, Set<Permission> permissions, Set<IPermissionGroup> groups) {
        IPermissionGroup group = getPermissionGroupByName(permissionGroup.getName());

        group.clearPermissions();
        group.clearInheritedPermissionGroups();

        for (Permission permission : permissions) group.addPermission(permission);
        for (IPermissionGroup inheritedGroup : groups) group.addInheritedPermissionGroup(inheritedGroup);
    }

    @Override
    public boolean existsGroup(String name) {
        return getPermissionGroupByName(name) != null;
    }

}
