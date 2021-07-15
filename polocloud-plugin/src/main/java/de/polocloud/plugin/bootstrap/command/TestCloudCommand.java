package de.polocloud.plugin.bootstrap.command;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.TemplateType;
import de.polocloud.plugin.api.CloudExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;


public class TestCloudCommand implements CommandExecutor {

    public TestCloudCommand() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                CloudExecutor.getInstance().getPubSubManager().subscribe("TestChannel", subscribePacket -> {
                    System.out.println("info from other lobby server  -> " + subscribePacket.getData());
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();


    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 1) {

            sender.sendMessage("requesting... " + args[0]);

            CloudExecutor.getInstance().getPubSubManager().publish("TestChannel", "Player " + sender.getName() + " is requesting something..");

            CloudExecutor.getInstance().getGameServerManager().getGameServersByType(TemplateType.valueOf(args[0])).thenAccept(list -> {

                for (IGameServer gameServer : list) {
                    sender.sendMessage(gameServer.getName() + " info");
                    sender.sendMessage("snowflake " + gameServer.getSnowflake() + "");
                    sender.sendMessage("status: " + gameServer.getStatus().toString());
                    sender.sendMessage("template: " + gameServer.getTemplate().getName());
                    sender.sendMessage(" - maintenance: " + gameServer.getTemplate().isMaintenance());
                    sender.sendMessage(" - Wrappers: " + Arrays.toString(gameServer.getTemplate().getWrapperNames()));
                    sender.sendMessage("--");
                }

            });

        } else {

            CloudExecutor.getInstance().getGameServerManager().getGameServers().thenAccept(servers -> {
                sender.sendMessage("Their are " + servers.size() + " Servers online");
            });

        }

        return true;
    }
}
