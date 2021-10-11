package de.polocloud.modules.serverselector.pluginside;

import de.polocloud.modules.serverselector.api.SignAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class SignPlugin extends JavaPlugin {

    private final SignAPI signAPI = new SignAPI(
        null,
        null,
        null
    );

    @Override
    public void onLoad() {

    }
}
