package de.polocloud.npcs.bootstraps;

import de.polocloud.npcs.npc.base.ICloudNPC;
import de.polocloud.npcs.plugin.PluginNPCService;
import de.polocloud.npcs.plugin.commands.NpcCommand;
import de.polocloud.npcs.plugin.listeners.CollectiveSpigotListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class PluginBootstrap extends JavaPlugin {

    private static PluginBootstrap instance;

    private PluginNPCService npcService;

    @Override
    public void onEnable() {
        instance = this;
        npcService = new PluginNPCService();
        Bukkit.getPluginManager().registerEvents(new CollectiveSpigotListener(), this);
        getCommand("cloudnpcs").setExecutor(new NpcCommand());
        getCommand("cloudnpcs").setAliases(Arrays.asList("npc", "npcs", "cloudnpcs"));
    }

    @Override
    public void onDisable() {
        for (ICloudNPC cloudNPC : npcService.getCloudNPCManager().getCloudNPCS()) {
            cloudNPC.remove();
        }
    }

    public static PluginBootstrap getInstance() {
        return instance;
    }

    public PluginNPCService getNpcService() {
        return npcService;
    }
}
