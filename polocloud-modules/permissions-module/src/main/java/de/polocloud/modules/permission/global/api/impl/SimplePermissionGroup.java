package de.polocloud.modules.permission.global.api.impl;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.util.Task;
import de.polocloud.modules.permission.PoloCloudPermissionModule;
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
    private final boolean defaultGroup;
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

    @Override
    public boolean hasPermission(String permission) {
        return this.permissions.contains(permission);
    }

    @Override
    public void removePermission(String permission) {
        this.permissions.remove(permission);
    }

    @Override
    public void addPermission(String permission) {
        this.permissions.add(permission);
    }

    @Override
    public void update() {
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
            PoloCloudPermissionModule.getInstance().getGroupDatabase().insert(this.name, this);
        } else {
            PoloCloudPermissionModule.getInstance().getMessageChannel().sendMessage(new Task(PoloCloudPermissionModule.TASK_NAME_UPDATE_GROUP, new JsonData("group", this)));
        }
    }
}
