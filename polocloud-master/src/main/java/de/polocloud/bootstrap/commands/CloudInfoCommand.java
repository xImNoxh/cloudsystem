package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

@CloudCommand.Info(name = "info", aliases = "", description = "")
public class CloudInfoCommand extends CloudCommand {

    private ITemplateService templateService;
    private IGameServerManager gameServerManager;

    public CloudInfoCommand(ITemplateService templateService, IGameServerManager gameServerManager) {
        this.templateService = templateService;
        this.gameServerManager = gameServerManager;
    }

    @Override
    public void execute(String[] args) {

        if(args.length == 3){

            String name = args[2];

            if(args[1].equalsIgnoreCase("template")){
                ITemplate template = templateService.getTemplateByName(name);

                if(template == null){
                    Logger.log(LoggerType.INFO, "no template founded.");
                    return;
                }
                Logger.log(LoggerType.INFO, "Template name: " + template.getName());
                Logger.log(LoggerType.INFO, "Maximal services: " + template.getMaxServerCount());
                Logger.log(LoggerType.INFO, "Minimal services: " + template.getMinServerCount());
                Logger.log(LoggerType.INFO, "Template type: " + template.getTemplateType().name());
                Logger.log(LoggerType.INFO, "Template version: " + template.getVersion().getTitle());
                return;
            }

            if(args[1].equalsIgnoreCase("wrapper")){



                return;
            }

            if(args[1].equalsIgnoreCase("service")){
                IGameServer gameServer = gameServerManager.getGameServerByName(name);

                if(gameServer == null){
                    Logger.log(LoggerType.INFO, "no gameserver founded.");
                    return;
                }

                Logger.log(LoggerType.INFO, "GameServer name: " + gameServer.getName());
                Logger.log(LoggerType.INFO, "GameServer port: " + gameServer.getPort());
                Logger.log(LoggerType.INFO, "GameServer number: " + gameServer.getSnowflake());
                Logger.log(LoggerType.INFO, "GameServer status: " + gameServer.getStatus());
                Logger.log(LoggerType.INFO, "GameServer started time: " + gameServer.getStartTime());
                return;
            }
        }
        Logger.log(LoggerType.INFO, ConsoleColors.LIGHT_BLUE.getAnsiCode() + "PoloCloud " + ConsoleColors.GRAY.getAnsiCode() + "» info <template/wrapper/service> <name-id>");
    }
}
