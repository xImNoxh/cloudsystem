package de.polocloud.modules.permission.api.impl;

import de.polocloud.api.logger.helper.MinecraftColor;
import de.polocloud.modules.permission.api.IPermissionDisplay;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class SimplePermissionDisplay implements IPermissionDisplay {

    /**
     * The color of the display
     */
    private final MinecraftColor color;

    /**
     * The prefix of the display
     */
    private final String prefix;

    /**
     * The suffix of the display
     */
    private final String suffix;

    /**
     * The chatFormat of the display
     */
    private final String chatFormat;

}
