package signs.bootstrap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import signs.SignService;

public class Bootstrap extends JavaPlugin {

    private static Bootstrap instance;

    @Override
    public void onEnable() {
        instance = this;
        new SignService();
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(Bootstrap.getInstance(), "BungeeCord");
    }

    @Override
    public void onDisable() {

    }

    public static Bootstrap getInstance() {
        return instance;
    }
}
