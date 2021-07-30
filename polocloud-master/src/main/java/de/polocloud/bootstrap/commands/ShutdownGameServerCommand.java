package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.util.concurrent.ExecutionException;

@CloudCommand.Info(name = "shutdown", description = "stop a current server", aliases = "", commandType = CommandType.CONSOLE)
public class ShutdownGameServerCommand extends CloudCommand {

    private IGameServerManager gameServerManager;

    public ShutdownGameServerCommand(IGameServerManager gameServerManager) {
        this.gameServerManager = gameServerManager;
    }

    @Override
    public void execute(ICommandExecutor commandSender, String[] args) {
        if (args.length == 2) {
            try {
                IGameServer gameServer = gameServerManager.getGameServerByName(args[1]).get();

                if (gameServer == null) {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "This service does not exists.");
                    return;
                }
                gameServer.stop();
                Logger.log(LoggerType.INFO, "You stopped the service " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getName());
                return;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use following command: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "shutdown <name-id>");
    }
}
