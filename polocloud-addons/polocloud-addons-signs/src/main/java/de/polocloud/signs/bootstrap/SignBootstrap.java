package de.polocloud.signs.bootstrap;

import de.polocloud.signs.SignService;
import de.polocloud.signs.collectives.CollectiveSpigotListener;
import de.polocloud.signs.commands.CloudSignsCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutionException;

public class SignBootstrap extends JavaPlugin {

    private static SignBootstrap instance;

    @Override
    public void onEnable() {
        instance = this;

        getCommand("cloudsigns").setExecutor(new CloudSignsCommand());

        try {
            new SignService();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        new CollectiveSpigotListener();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {

    }

    public static SignBootstrap getInstance() {
        return instance;
    }
}
