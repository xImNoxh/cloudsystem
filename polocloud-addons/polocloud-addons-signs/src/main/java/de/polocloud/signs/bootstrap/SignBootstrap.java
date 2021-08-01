package de.polocloud.signs.bootstrap;

import de.polocloud.signs.SignService;
import de.polocloud.signs.collectives.CollectiveSpigotListener;
import de.polocloud.signs.commands.CloudSignsCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class SignBootstrap extends JavaPlugin {

    private static SignBootstrap instance;

    @Override
    public void onEnable() {
        instance = this;

        getCommand("cloudsigns").setExecutor(new CloudSignsCommand());

        try {
            new SignService();
        } catch (ExecutionException | InterruptedException exception) {
            exception.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, "Failed to start SignService! The SignAddon will react abnormal.\n" +
                "Please report this error.");
        }

        new CollectiveSpigotListener();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
        SignService.getInstance().getSignProtectionRunnable().getTask().cancel();
    }

    public static SignBootstrap getInstance() {
        return instance;
    }
}
