package de.polocloud.modules.permission.global.api.impl;

import de.polocloud.modules.permission.global.api.IPermission;
import de.polocloud.modules.permission.global.api.IPermissionDisplay;
import de.polocloud.modules.permission.global.api.IPermissionGroup;
import de.polocloud.modules.permission.global.api.PermissionPool;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter @AllArgsConstructor
public class SimplePermissionGroup implements IPermissionGroup {

    private final String name;
    private final int id;
    private final List<String> permissions;
    private final SimplePermissionDisplay display;
    private final List<String> inheritances;

    public IPermissionDisplay getDisplay() {
        return display;
    }

    public List<String> getInheritancesStringBased() {
        return inheritances;
    }

    public List<IPermissionGroup> getInheritances() {
        List<IPermissionGroup> list = new ArrayList<>();
        for (String inheritance : this.inheritances) {
            PermissionPool.getInstance().getAllCachedPermissionGroups().stream().filter(iPermissionGroup -> iPermissionGroup.getName().equalsIgnoreCase(inheritance)).findFirst().ifPresent(list::add);
        }

        return list;
    }
}
