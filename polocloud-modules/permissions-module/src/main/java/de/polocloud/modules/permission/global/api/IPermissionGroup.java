package de.polocloud.modules.permission.global.api;

import de.polocloud.api.common.INamable;

import java.util.List;

public interface IPermissionGroup extends INamable, Expireable {

    /**
     * The priority (id) of this group
     */
    int getId();

    /**
     * If this group is the default group
     */
    boolean isDefaultGroup();

    /**
     * The display settings
     */
    IPermissionDisplay getDisplay();

    /**
     * All permissions of this group
     */
    List<String> getPermissions();

    /**
     * The list of groups this group extends its
     * permissions from
     */
    List<IPermissionGroup> getInheritances();
}
