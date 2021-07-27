package de.polocloud.signs.bootstrap;

import de.polocloud.signs.SignService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SignBootstrap extends JavaPlugin {

    private static SignBootstrap instance;

    @Override
    public void onEnable() {
        instance = this;
        new SignService();
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(SignBootstrap.getInstance(), "BungeeCord");
    }

    @Override
    public void onDisable() {

    }

    public static SignBootstrap getInstance() {
        return instance;
    }
}
