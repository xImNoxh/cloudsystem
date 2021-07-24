package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerExecuteCommandPacket;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.util.concurrent.ExecutionException;

@CloudCommand.Info(name = "execute", description = "send a command to a gameserver", aliases = "", commandType = CommandType.CONSOLE)
public class GameServerExecuteCommand extends CloudCommand {

    private IGameServerManager gameServerManager;

    public GameServerExecuteCommand(IGameServerManager gameServerManager) {
        this.gameServerManager = gameServerManager;
    }

    @Override
    public void execute(ICommandExecutor commandSender, String[] args) {
        if (args.length >= 3) {
            try {
                IGameServer server = gameServerManager.getGameServerByName(args[1]).get();

                if (server == null) {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "This service does not exists.");
                    return;
                }

                StringBuilder builder = new StringBuilder();

                for (int i = 2; i < args.length; i++) {
                    builder.append(args[i]).append(" ");
                }
                server.sendPacket(new GameServerExecuteCommandPacket(builder.toString()));
                Logger.log(LoggerType.INFO, Logger.PREFIX + "You execute a command to " + server.getName() + " (" + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "/" + builder.substring(0, builder.length() - 1) + ConsoleColors.GRAY.getAnsiCode() + ")");
                return;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use following command: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "execute <name-id> <command>");
    }
}
