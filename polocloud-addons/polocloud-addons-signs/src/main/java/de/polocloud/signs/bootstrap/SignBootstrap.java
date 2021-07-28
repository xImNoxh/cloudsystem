package de.polocloud.signs.bootstrap;

import de.polocloud.signs.SignService;
import de.polocloud.signs.commands.CloudSignsCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SignBootstrap extends JavaPlugin {

    private static SignBootstrap instance;

    @Override
    public void onEnable() {
        instance = this;
        new SignService();
        Bukkit.getPluginCommand("cloudsigns").setExecutor(new CloudSignsCommand());
    }

    @Override
    public void onDisable() {

    }

    public static SignBootstrap getInstance() {
        return instance;
    }
}
