package de.polocloud.plugin.bootstrap.command;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.TemplateType;
import de.polocloud.plugin.api.CloudExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;


public class TestCloudCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 1) {

            sender.sendMessage("requesting Player... " + args[0]);

            CloudExecutor.getInstance().getCloudPlayerManager().getOnlinePlayer(args[0]).thenAccept(player -> {
                sender.sendMessage(player.getName());
                sender.sendMessage(player.getUUID().toString());
                sender.sendMessage(player.getProxyServer().getName());
                sender.sendMessage(player.getMinecraftServer().getName());
            });

            /*
            CloudExecutor.getInstance().getGameServerManager().getGameServerByName(args[0]).thenAccept(s -> {

                CloudExecutor.getInstance().getGameServerManager().getGameServersByTemplate(s.getTemplate()).thenAccept(servers -> {

                    for (IGameServer gameServer : servers) {
                        sender.sendMessage(gameServer.getName() + " info");
                        sender.sendMessage("snowflake " + gameServer.getSnowflake() + "");
                        sender.sendMessage("status: " + gameServer.getStatus().toString());
                        sender.sendMessage("template: " + gameServer.getTemplate().getName());
                        sender.sendMessage(" - maintenance: " + gameServer.getTemplate().isMaintenance());
                        sender.sendMessage(" - Wrappers: " + Arrays.toString(gameServer.getTemplate().getWrapperNames()));
                        sender.sendMessage("--");
                    }


                });


            });

             */

        } else {

            CloudExecutor.getInstance().getGameServerManager().getGameServers().thenAccept(servers -> {
                sender.sendMessage("Their are " + servers.size() + " Servers online");
            });

        }

        return true;
    }
}
