package de.polocloud.modules.permission.api.impl;

import de.polocloud.modules.permission.api.IPermission;
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

    @Override
    public boolean equals(String perms) {
        return this.permission.equalsIgnoreCase(perms);
    }

    @Override
    public boolean isTemporary() {
        return expiringTime != -1L;
    }
}
