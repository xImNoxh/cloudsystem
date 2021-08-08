package de.polocloud.permission.services.bootstrap.spigot;

import de.polocloud.permission.services.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotBootstrap extends JavaPlugin {

    @Override
    public void onEnable() {

        new Permissions(false);

        Bukkit.getPluginManager().registerEvents(new SpigotCollectiveListener(), this);
    }

    @Override
    public void onDisable() {
        Permissions.getInstance().getDatabaseService().getDatabaseSQL().close();
    }


}
