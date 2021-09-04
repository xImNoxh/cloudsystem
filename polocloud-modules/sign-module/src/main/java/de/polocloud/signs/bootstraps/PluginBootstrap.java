package de.polocloud.signs.bootstraps;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.signs.plugin.SignsPluginService;
import de.polocloud.signs.plugin.commands.SignCommand;
import de.polocloud.signs.plugin.listeners.CollectivesCloudListener;
import de.polocloud.signs.plugin.listeners.CollectivesSpigotListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginBootstrap extends JavaPlugin {

    private static PluginBootstrap instance;
    private SignsPluginService signService;

    @Override
    public void onEnable() {
        instance = this;
        signService = new SignsPluginService();
        getCommand("cloudsigns").setExecutor(new SignCommand());
        Bukkit.getPluginManager().registerEvents(new CollectivesSpigotListener(), this);
        PoloCloudAPI.getInstance().getEventManager().registerListener(new CollectivesCloudListener());
    }

    @Override
    public void onDisable() {
        signService.getSignAnimator().stopAnimation();
    }

    public static PluginBootstrap getInstance() {
        return instance;
    }

    public SignsPluginService getSignService() {
        return signService;
    }
}
