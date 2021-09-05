package de.polocloud.modules.permission.api.impl;

import de.polocloud.modules.permission.api.IPermission;
import de.polocloud.modules.permission.api.IPermissionDisplay;
import de.polocloud.modules.permission.api.IPermissionGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter @AllArgsConstructor
public class SimplePermissionGroup implements IPermissionGroup {

    private final String name;
    private final int id;
    private final List<SimplePermission> permissions;
    private final SimplePermissionDisplay display;
    private final List<String> inheritances;


    public IPermissionDisplay getDisplay() {
        return display;
    }

    public List<IPermission> getPermissions() {
        return new ArrayList<>(permissions);
    }

    //TODO
    public List<IPermissionGroup> getInheritances() {
        return new ArrayList<>();
    }
}
