package de.polocloud.npcs.module.config;

import de.polocloud.api.config.IConfig;

public class NPCConfig implements IConfig {

    private final String commandPermission = "cloud.npc.command.use";

    private final String selectorInventoryTitle = "§eSelector §7(§b%template%§7)";

    public String getSelectorInventoryTitle() {
        return selectorInventoryTitle;
    }

    public String getCommandPermission() {
        return commandPermission;
    }
}
