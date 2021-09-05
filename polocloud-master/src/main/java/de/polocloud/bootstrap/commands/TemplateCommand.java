package de.polocloud.bootstrap.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Arguments;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.annotation.CommandExecutors;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.template.helper.GameServerVersion;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.ITemplateManager;
import de.polocloud.bootstrap.setup.CreateTemplateSetup;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.api.logger.helper.LogLevel;

import java.util.List;

public class TemplateCommand implements CommandListener {

    private ITemplateManager templateService;
    private IGameServerManager gameServerManager;

    public TemplateCommand(ITemplateManager templateService, IGameServerManager gameServerManager) {
        this.templateService = templateService;
        this.gameServerManager = gameServerManager;
    }

    @Command(
        name = "template",
        description = "Manage a template",
        aliases = "group"
    )
    @CommandExecutors(ExecutorType.CONSOLE)
    public void execute(CommandExecutor sender, String[] fullArgs, @Arguments(min = 1, max = 7) String... params) {
        if (params.length == 1) {
            if (params[0].equalsIgnoreCase("create")) {
                new CreateTemplateSetup(templateService).sendSetup();
                PoloCloudAPI.getInstance().reload();
            } else if (params[0].equalsIgnoreCase("versions")) {
                PoloLogger.print(LogLevel.INFO, "Available Versions » ");
                for (GameServerVersion value : GameServerVersion.values()) {
                    PoloLogger.print(LogLevel.INFO, value.getTitle());
                }
            } else {
                sendHelp();
            }
        } else if (params.length == 2) {
            if (params[0].equalsIgnoreCase("shutdown") || params[0].equalsIgnoreCase("stop")) {
                String templateName = params[1];
                ITemplate template = templateService.getTemplate(templateName);
                if (template == null) {
                    PoloLogger.print(LogLevel.WARNING, "The template » " + ConsoleColors.LIGHT_BLUE + templateName + ConsoleColors.GRAY + " doesn't exists!");
                } else {
                    PoloLogger.print(LogLevel.INFO, "Stopping template » " + ConsoleColors.LIGHT_BLUE + template.getName() + ConsoleColors.GRAY + "...");
                    List<IGameServer> gameServersInTemplate = gameServerManager.getCached(template);
                    int size = gameServersInTemplate.size();
                    for (IGameServer gameServer : gameServersInTemplate) {
                        gameServer.stop();
                    }
                    PoloLogger.print(LogLevel.INFO, ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "stopped " + ConsoleColors.LIGHT_BLUE + size + ConsoleColors.GRAY + " servers of template » " + ConsoleColors.LIGHT_BLUE + template.getName() + ConsoleColors.GRAY + "!");
                }
            } else if (params[0].equalsIgnoreCase("info")) {
                String templateName = params[1];
                ITemplate template = templateService.getTemplate(templateName);
                if (template == null) {
                    PoloLogger.print(LogLevel.WARNING, "The template » " + ConsoleColors.LIGHT_BLUE + templateName + ConsoleColors.GRAY + " doesn't exists!");
                } else {
                    PoloLogger.print(LogLevel.INFO, "----[Information]----");
                    PoloLogger.print(LogLevel.INFO, "Template name » " + template.getName());
                    PoloLogger.print(LogLevel.INFO, "Maximal services » " + template.getMaxServerCount());
                    PoloLogger.print(LogLevel.INFO, "Minimal services » " + template.getMinServerCount());
                    PoloLogger.print(LogLevel.INFO, "Template type » " + template.getTemplateType().name());
                    PoloLogger.print(LogLevel.INFO, "Template version » " + template.getVersion().getTitle());
                    PoloLogger.print(LogLevel.INFO, "----[/Information]----");
                }
            } else {
                sendHelp();
            }
        } else if (params.length == 5) {
            if (params[0].equalsIgnoreCase("edit")) {
                String templateName = params[1];
                ITemplate template = templateService.getTemplate(templateName);
                if (template == null) {
                    PoloLogger.print(LogLevel.WARNING, "The template » " + ConsoleColors.LIGHT_BLUE + templateName + ConsoleColors.GRAY + " doesn't exists!");
                } else {

                    if (params[2].equalsIgnoreCase("set")) {
                        if (params[3].equalsIgnoreCase("maintenance")) {
                            if (!(params[4].equalsIgnoreCase("true") || params[4].equalsIgnoreCase("false"))) {
                                PoloLogger.print(LogLevel.ERROR, "Please provide a state (boolean (true, false))");
                                return;
                            }

                            PoloLogger.print(LogLevel.INFO, "updating...");
                            boolean state = Boolean.parseBoolean(params[4]);

                            template.setMaintenance(state);
                            templateService.getTemplateSaver().save(template);

                            for (IGameServer gameServer : gameServerManager.getCached(template)) {
                                gameServer.update();
                            }

                            PoloLogger.print(LogLevel.INFO, ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "updated the maintenance state of the template » " + ConsoleColors.LIGHT_BLUE + template.getName() + ConsoleColors.GRAY + "! (New state » " + ConsoleColors.LIGHT_BLUE + state + ConsoleColors.GRAY + ")");
                        } else if (params[3].equalsIgnoreCase("maxplayers")) {
                            String amountString = params[4];
                            int amount;
                            try {
                                amount = Integer.parseInt(amountString);
                            } catch (NumberFormatException exception) {
                                PoloLogger.print(LogLevel.ERROR, "Please provide a real number (int)");
                                return;
                            }


                            template.setMaxPlayers(Integer.parseInt(params[4]));
                            templateService.getTemplateSaver().save(template);

                            PoloCloudAPI.getInstance().updateCache();
                            PoloLogger.print(LogLevel.INFO, ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "updated the maximal players of the template » " + ConsoleColors.LIGHT_BLUE + template.getName() + ConsoleColors.GRAY + "! (New state » " + ConsoleColors.LIGHT_BLUE + amount + ConsoleColors.GRAY + ")");
                        } else {
                            PoloLogger.print(LogLevel.INFO, "----[Template-Edit]----");
                            PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "template edit <template> set maintenance <state (boolean(true, false))> " + ConsoleColors.GRAY + "to set the maintenance mode of a template");
                            PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "template edit <template> set maxplayers <amount> " + ConsoleColors.GRAY + "to set the maximal players of a template");
                            PoloLogger.print(LogLevel.INFO, "----[Template-Edit]----");
                        }
                    } else {
                        
                        PoloLogger.print(LogLevel.INFO, "----[Template-Edit]----");
                        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "template edit <template> set maintenance <state (boolean(true, false))> " + ConsoleColors.GRAY + "to set the maintenance mode of a template");
                        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "template edit <template> set maxplayers <amount> " + ConsoleColors.GRAY + "to set the maximal players of a template");
                        PoloLogger.print(LogLevel.INFO, "----[/Template-Edit]----");
                    }
                }
            } else {
                sendHelp();
            }
        } else {
            sendHelp();
        }

    }

    private void sendHelp() {
        PoloLogger.print(LogLevel.INFO, "----[Template]----");
        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "template create " + ConsoleColors.GRAY + "to create a new template");
        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "template versions " + ConsoleColors.GRAY + "to show all available versions for a template");
        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "template info <template> " + ConsoleColors.GRAY + "to get information about a template");
        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "template shutdown <template> " + ConsoleColors.GRAY + "to shutdown a entire template");
        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "template edit <template> set maintenance <state (boolean(true, false))> " + ConsoleColors.GRAY + "to set the maintenance mode of a template");
        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "template edit <template> set maxplayers <amount> " + ConsoleColors.GRAY + "to set the maximal players of a template");
        PoloLogger.print(LogLevel.INFO, "----[/Template]----");
    }
}
