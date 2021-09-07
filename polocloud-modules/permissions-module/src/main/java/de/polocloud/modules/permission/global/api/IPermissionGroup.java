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

    /**
     * Removes a permission from this group
     *
     * @param permission the permission
     */
    void removePermission(String permission);

    /**
     * Checks if this group has a permission
     *
     * @param permission the permission
     */
    boolean hasPermission(String permission);

    /**
     * Adds a permission to this group
     *
     * @param permission the permission
     */
    void addPermission(String permission);

    /**
     * Updates this group
     */
    void update();
}
