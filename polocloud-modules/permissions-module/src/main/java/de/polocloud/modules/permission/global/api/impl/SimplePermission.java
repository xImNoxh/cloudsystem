package de.polocloud.modules.permission.global.api.impl;

import de.polocloud.modules.permission.global.api.IPermission;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class SimplePermission implements IPermission {

    /**
     * The permission string
     */
    private final String permission;

    /**
     * The time it expires
     */
    private final long expiringTime;

    public SimplePermission(String permission) {
        this(permission, -1L);
    }

    @Override
    public boolean equals(String perms) {
        return this.permission.equalsIgnoreCase(perms);
    }

    @Override
    public boolean isTemporary() {
        return expiringTime != -1L;
    }
}
