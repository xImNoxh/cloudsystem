package de.polocloud.plugin.executes.call;

import de.polocloud.plugin.executes.CloudCommandExecute;
import net.md_5.bungee.api.ProxyServer;

public class BungeeCommandCall implements CloudCommandExecute {

    @Override
    public void callExecute(String command) {
        ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command.substring(0, command.length()-1));
    }
}
