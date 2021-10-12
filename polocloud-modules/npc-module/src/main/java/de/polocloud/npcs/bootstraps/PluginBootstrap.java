package de.polocloud.npcs.bootstraps;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.npcs.npc.base.ICloudNPC;
import de.polocloud.npcs.plugin.PluginNPCService;
import de.polocloud.npcs.plugin.commands.NpcCommand;
import de.polocloud.npcs.plugin.listeners.CollectiveSpigotListener;
import de.polocloud.npcs.protocol.GlobalConfigClass;
import de.polocloud.npcs.protocol.enumeration.RequestType;
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
        npcService.shutdown();
        instance = null;
    }

    public static PluginBootstrap getInstance() {
        return instance;
    }

    public PluginNPCService getNpcService() {
        return npcService;
    }
}
