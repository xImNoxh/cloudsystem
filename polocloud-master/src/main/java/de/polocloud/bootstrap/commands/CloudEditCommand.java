package de.polocloud.bootstrap.commands;

import com.google.inject.Inject;
import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerMaintenanceUpdatePacket;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.io.File;
import java.util.concurrent.ExecutionException;

@CloudCommand.Info(name = "edit", description = "edit templates", aliases = "")
public class CloudEditCommand extends CloudCommand {

    private ITemplateService templateService;
    private IGameServerManager gameServerManager;

    private MasterConfig masterConfig;

    public CloudEditCommand(ITemplateService templateService, IGameServerManager gameServerManager) {
        this.templateService = templateService;
        this.gameServerManager = gameServerManager;

        File configFile = new File("config.json");
        IConfigLoader configLoader = new SimpleConfigLoader();
        masterConfig = configLoader.load(MasterConfig.class, configFile);
    }

    @Override
    public void execute(String[] args) {

        if(args.length == 6){

            if(args[1].equalsIgnoreCase("template")){

                ITemplate template = templateService.getTemplateByName(args[2]);

                if(template == null){
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "This template is not exists.");
                    return;
                }

                if(args[3].equalsIgnoreCase("set")){
                    if(args[4].equalsIgnoreCase("maintenance")){

                        if(!(args[5].equalsIgnoreCase("true") || args[5].equalsIgnoreCase("false"))){
                            Logger.log(LoggerType.INFO,Logger.PREFIX +  "Use following command: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() +
                                "edit template <template> set maintenance <true/false>");
                            return;
                        }

                        Boolean state = Boolean.parseBoolean(args[5]);

                        template.setMaintenance(state);
                        templateService.getTemplateSaver().save(template);

                        try {
                            for (IGameServer gameServer : gameServerManager.getGameServersByTemplate(template).get()) {
                                gameServer.sendPacket(new GameServerMaintenanceUpdatePacket(template.isMaintenance(),
                                    gameServer.getTemplate().getTemplateType() == TemplateType.PROXY ?
                                        masterConfig.getMessages().getProxyMaintenanceMessage() : masterConfig.getMessages().getGroupMaintenanceMessage()));
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        Logger.log(LoggerType.INFO, Logger.PREFIX + "You update the " + ConsoleColors.CYAN.getAnsiCode() + "maintenance " + ConsoleColors.GRAY.getAnsiCode() + "state for template "
                            + ConsoleColors.LIGHT_BLUE.getAnsiCode() + template.getName());
                        return;
                    }
                }
            }
        }
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use following command: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() +
            "edit template <template> set maintenance <true/false>");
    }
}
