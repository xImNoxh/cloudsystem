package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerCopyPacket;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.client.WrapperClient;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@CloudCommand.Info(name = "copy", description = "Copies a tmp GameServer to the template of the server", aliases = "", commandType = CommandType.CONSOLE)
public class GameServerCopyCommand extends CloudCommand {

    private IGameServerManager gameServerManager;

    public GameServerCopyCommand(IGameServerManager gameServerManager) {
        this.gameServerManager = gameServerManager;
    }

    @Override
    public void execute(ICommandExecutor sender, String[] args) {
        try {
            if (args.length == 3) {
                String name = args[1];
                String type = args[2];
                IGameServer gameServer = gameServerManager.getGameServerByName(name).get();
                if (gameServer == null) {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "No gameserver found!");
                    return;
                }
                if (!gameServer.getStatus().equals(GameServerStatus.RUNNING)) {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "This gameserver is not completely stopped or started!");
                    return;
                }
                if (type.equalsIgnoreCase("worlds")) {
                    if (gameServer.getTemplate().getTemplateType().equals(TemplateType.PROXY)) {
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "A Proxy Server doesn't have worlds, please use the 'entire' type!");
                        return;
                    }
                    List<WrapperClient> wrappers = Master.getInstance().getWrapperClientManager().getWrapperClients().stream().filter(wrapperClient -> Arrays.asList(gameServer.getTemplate().getWrapperNames()).contains(wrapperClient.getName())).collect(Collectors.toList());
                    for (WrapperClient wrapper : wrappers) {
                        wrapper.sendPacket(new APIRequestGameServerCopyPacket(APIRequestGameServerCopyPacket.Type.WORLD, gameServer.getName(), String.valueOf(gameServer.getSnowflake()), gameServer.getTemplate().getName()));
                    }
                } else if (type.equalsIgnoreCase("entire")) {
                    List<WrapperClient> wrappers = Master.getInstance().getWrapperClientManager().getWrapperClients().stream().filter(wrapperClient -> Arrays.asList(gameServer.getTemplate().getWrapperNames()).contains(wrapperClient.getName())).collect(Collectors.toList());
                    for (WrapperClient wrapper : wrappers) {
                        wrapper.sendPacket(new APIRequestGameServerCopyPacket(APIRequestGameServerCopyPacket.Type.ENTIRE, gameServer.getName(), String.valueOf(gameServer.getSnowflake()), gameServer.getTemplate().getName()));
                    }
                } else {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Use following command: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() +
                        "copy <gameserver> entire/worlds");
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Explanation: \n" +
                        Logger.PREFIX + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "entire: " + ConsoleColors.GRAY.getAnsiCode() + "copies the entire GameServer to the template\n" +
                        Logger.PREFIX + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "worlds: " + ConsoleColors.GRAY.getAnsiCode() + "only copies the worlds of the GameServer to the template");
                }
            } else {
                Logger.log(LoggerType.INFO, Logger.PREFIX + "Use following command: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() +
                    "copy <gameserver> entire/worlds");
                Logger.log(LoggerType.INFO, Logger.PREFIX + "Explanation: \n" +
                    Logger.PREFIX + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "entire: " + ConsoleColors.GRAY.getAnsiCode() + "copies the entire GameServer to the template\n" +
                    Logger.PREFIX + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "worlds: " + ConsoleColors.GRAY.getAnsiCode() + "only copies the worlds of the GameServer to the template");
            }
        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
        }
    }
}
