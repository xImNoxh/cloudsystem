package de.polocloud.npcs.module.config;

import de.polocloud.api.config.IConfig;

public class NPCConfig implements IConfig {

    private String commandPermission = "cloud.npc.command.use";

    public String getCommandPermission() {
        return commandPermission;
    }
}
