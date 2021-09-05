package de.polocloud.modules.permission.api;

import de.polocloud.api.logger.helper.MinecraftColor;

public interface IPermissionDisplay {

    /**
     * The color of this group
     */
    MinecraftColor getColor();

    /**
     * The prefix of this display
     */
    String getPrefix();

    /**
     * The suffix of this display
     */
    String getSuffix();

    /**
     * The format for the chat
     */
    String getChatFormat();

}
