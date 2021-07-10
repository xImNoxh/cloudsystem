package de.polocloud.plugin.spigot;

import de.polocloud.plugin.CloudBootstrap;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PoloCloudPlugin extends JavaPlugin {


    @Override
    public void onEnable() {

        CloudBootstrap bootstrap = new CloudBootstrap();
        bootstrap.connect(Bukkit.getPort());

        System.out.println("PoloCloudPlugin enalbing");
    }
}
