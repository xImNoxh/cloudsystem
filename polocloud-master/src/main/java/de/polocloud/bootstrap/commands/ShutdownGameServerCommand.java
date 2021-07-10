package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

@CloudCommand.Info(name = "shutdown", description = "stop a current server", aliases = "")
public class ShutdownGameServerCommand extends CloudCommand {

    private IGameServerManager gameServerManager;

    public ShutdownGameServerCommand(IGameServerManager gameServerManager) {
        this.gameServerManager = gameServerManager;
    }

    @Override
    public void execute(String[] args) {

        if(args.length == 2) {
            IGameServer gameServer = gameServerManager.getGameServerByName(args[1]);
            if(gameServer == null){
                Logger.log(LoggerType.INFO, "This group does not exists.");
                return;
            }
            gameServer.stop();
            Logger.log(LoggerType.INFO, "You stopped the service " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getName());
            return;
        }

        Logger.log(LoggerType.INFO, "Use follwoing command: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "shutdown <name-id>");

    }
}
