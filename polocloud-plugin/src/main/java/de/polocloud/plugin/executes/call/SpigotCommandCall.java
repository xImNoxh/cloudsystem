package de.polocloud.plugin.executes.call;

import de.polocloud.plugin.executes.CloudCommandExecute;
import org.bukkit.Bukkit;

public class SpigotCommandCall implements CloudCommandExecute {

    @Override
    public void callExecute(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
