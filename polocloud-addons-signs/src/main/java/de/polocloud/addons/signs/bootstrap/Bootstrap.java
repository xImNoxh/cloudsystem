package de.polocloud.addons.signs.bootstrap;

import de.polocloud.addons.signs.SignService;
import org.bukkit.plugin.java.JavaPlugin;

public class Bootstrap extends JavaPlugin {

    private static Bootstrap instance;

    @Override
    public void onEnable() {
        instance = this;
        new SignService();
    }

    @Override
    public void onDisable() {

    }

    public static Bootstrap getInstance() {
        return instance;
    }
}
