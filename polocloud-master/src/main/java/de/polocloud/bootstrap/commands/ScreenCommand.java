package de.polocloud.bootstrap.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ConsoleExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.packets.wrapper.WrapperRequestLinesPacket;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;
import de.polocloud.api.network.protocol.packet.base.response.def.Response;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ScreenCommand implements CommandListener {


    @Command(name = "screen", description = "Shows ouput of a screen", aliases = "sc")
    public void execute(CommandExecutor sender, String[] fullArgs, String... params) {
        if (params.length == 1) {

            String serverName = params[0];
            IGameServer gameServer = PoloCloudAPI.getInstance().getGameServerManager().getCached(serverName);
            if (gameServer != null) {

                List<String> cachedLines = PacketMessenger.create().blocking().orElse(new Response(new JsonData().append("lines", new LinkedList<>()))).timeOutAfter(TimeUnit.SECONDS, 2L).blocking().send(new WrapperRequestLinesPacket(gameServer.getName())).get("lines").getAsCustom(List.class);

                if (cachedLines.isEmpty()) {
                    PoloLogger.print(LogLevel.WARNING, "§7This screen doesn't have anything to §edisplay §7yet!");
                    return;
                }

                PoloLogger.print(LogLevel.INFO, "§7Here are the last lines of §2" + gameServer.getName() + " §7!");

                for (String cachedLine : cachedLines) {
                    PoloLogger.print(LogLevel.INFO, "§7[§b" + gameServer.getName() + "§7] §7" + cachedLine);
                }
            } else {
                PoloLogger.print(LogLevel.WARNING, "§7The server §e" + serverName + " §7seems not to be online!");
            }
        } else {
            sender.sendMessage("§cUse §escreen <server> §c!");
        }
    }
}
