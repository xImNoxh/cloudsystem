package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.util.concurrent.ExecutionException;

@CloudCommand.Info(name = "info", aliases = "", description = "", commandType = CommandType.CONSOLE)
public class CloudInfoCommand extends CloudCommand {

    private ITemplateService templateService;
    private IGameServerManager gameServerManager;

    public CloudInfoCommand(ITemplateService templateService, IGameServerManager gameServerManager) {
        this.templateService = templateService;
        this.gameServerManager = gameServerManager;
    }

    @Override
    public void execute(ICommandExecutor commandSender, String[] args) {

        if (args.length == 3) {

            String name = args[2];

            if (args[1].equalsIgnoreCase("template")) {
                try {
                    ITemplate template = templateService.getTemplateByName(name).get();

                    if (template == null) {
                        Logger.log(LoggerType.INFO, "No template found.");
                        return;
                    }
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Template name: " + template.getName());
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Maximal services: " + template.getMaxServerCount());
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Minimal services: " + template.getMinServerCount());
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Template type: " + template.getTemplateType().name());
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Template version: " + template.getVersion().getTitle());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return;
            }

            if (args[1].equalsIgnoreCase("wrapper")) {


                return;
            }

            if (args[1].equalsIgnoreCase("service")) {
                try {
                    IGameServer gameServer = gameServerManager.getGameServerByName(name).get();


                    if (gameServer == null) {
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "No gameserver founded.");
                        return;
                    }

                    Logger.newLine();
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "GameServer name: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getName());
                    Logger.newLine();
                    Logger.log(LoggerType.INFO,
                        Logger.PREFIX + "Total memory: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getTotalMemory() + "mb");
                    Logger.log(LoggerType.INFO,
                        Logger.PREFIX + "Id: #" + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getSnowflake());
                    Logger.log(LoggerType.INFO,
                        Logger.PREFIX + "Status: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getStatus());
                    Logger.log(LoggerType.INFO,
                        Logger.PREFIX + "Started time: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getStartTime());
                    Logger.log(LoggerType.INFO,
                        Logger.PREFIX + "Ping: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getPing() + "ms");
                    return;
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        Logger.log(LoggerType.INFO, Logger.PREFIX + "info <template/wrapper/service> <name-id>");
    }
}
