package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerExecutePacket;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

@CloudCommand.Info(name = "execute", description = "send a command to a gameserver", aliases = "")
public class GameServerExecuteCommand extends CloudCommand {

    private IGameServerManager gameServerManager;

    public GameServerExecuteCommand(IGameServerManager gameServerManager) {
        this.gameServerManager = gameServerManager;
    }

    @Override
    public void execute(String[] args) {

        if(args.length >= 2){

            IGameServer server = gameServerManager.getGameServerByName(args[1]);

            if(server == null){
                Logger.log(LoggerType.INFO, Logger.PREFIX + "This service does not exists.");
                return;
            }

            StringBuilder builder = new StringBuilder();

            for(int i = 2; i< args.length; i++){
                builder.append(args[i]).append(" ");
            }
            server.sendPacket(new GameServerExecutePacket(builder.toString()));
            Logger.log(LoggerType.INFO, Logger.PREFIX + "You execute a command to " + server.getName() + " with input = " + builder);
            return;
        }
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use following command: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "execute <name-id> <command>");
    }
}
