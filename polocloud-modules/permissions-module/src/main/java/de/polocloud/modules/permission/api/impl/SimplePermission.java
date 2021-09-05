package de.polocloud.modules.permission.api.impl;

import de.polocloud.modules.permission.api.IPermission;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class SimplePermission implements IPermission {

    private final String permission;
    private final long expiringTime;

    @Override
    public boolean isTemporary() {
        return expiringTime != -1L;
    }
}
