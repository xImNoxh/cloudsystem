package de.polocloud.bootstrap.commands;

import de.polocloud.api.command.annotation.Arguments;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.annotation.CommandExecutors;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.template.GameServerVersion;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.setup.CreateTemplateSetup;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TemplateCommand implements CommandListener {

    private ITemplateService templateService;
    private IGameServerManager gameServerManager;

    public TemplateCommand(ITemplateService templateService, IGameServerManager gameServerManager) {
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
        try {
            if (params.length == 1) {
                if (params[1].equalsIgnoreCase("create")) {
                    new CreateTemplateSetup(templateService).sendSetup();
                    return;
                } else if (params[0].equalsIgnoreCase("versions")) {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Available Versions » ");
                    Logger.newLine();
                    for (GameServerVersion value : GameServerVersion.values()) {
                        Logger.log(LoggerType.INFO, value.getTitle());
                    }
                    return;
                } else {
                    sendHelp();
                }
            } else if (params.length == 2) {
                if (params[0].equalsIgnoreCase("shutdown") || params[0].equalsIgnoreCase("stop")) {
                    String templateName = params[1];
                    ITemplate template = templateService.getTemplateByName(templateName).get();
                    if (template == null) {
                        Logger.log(LoggerType.WARNING, Logger.PREFIX + "The template » " + ConsoleColors.LIGHT_BLUE + templateName + ConsoleColors.GRAY + " doesn't exists!");
                    } else {
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "Stopping template » " + ConsoleColors.LIGHT_BLUE + template.getName() + ConsoleColors.GRAY + "...");
                        List<IGameServer> gameServersInTemplate = gameServerManager.getGameServersByTemplate(template).get();
                        int size = gameServersInTemplate.size();
                        for (IGameServer gameServer : gameServersInTemplate) {
                            gameServer.stop();
                        }
                        Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "stopped " + ConsoleColors.LIGHT_BLUE + size + ConsoleColors.GRAY + " servers of template » " + ConsoleColors.LIGHT_BLUE + template.getName() + ConsoleColors.GRAY + "!");
                    }
                    return;
                } else if (params[0].equalsIgnoreCase("info")) {
                    String templateName = params[1];
                    ITemplate template = templateService.getTemplateByName(templateName).get();
                    if (template == null) {
                        Logger.log(LoggerType.WARNING, Logger.PREFIX + "The template » " + ConsoleColors.LIGHT_BLUE + templateName + ConsoleColors.GRAY + " doesn't exists!");
                    } else {
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Information]----");
                        Logger.newLine();
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "Template name » " + template.getName());
                        Logger.newLine();
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "Maximal services » " + template.getMaxServerCount());
                        Logger.newLine();
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "Minimal services » " + template.getMinServerCount());
                        Logger.newLine();
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "Template type » " + template.getTemplateType().name());
                        Logger.newLine();
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "Template version » " + template.getVersion().getTitle());
                        Logger.newLine();
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[/Information]----");
                    }
                    return;
                } else {
                    sendHelp();
                }
            } else if (params.length == 5) {
                if (params[0].equalsIgnoreCase("edit")) {
                    String templateName = params[1];
                    ITemplate template = templateService.getTemplateByName(templateName).get();
                    if (template == null) {
                        Logger.log(LoggerType.WARNING, Logger.PREFIX + "The template » " + ConsoleColors.LIGHT_BLUE + templateName + ConsoleColors.GRAY + " doesn't exists!");
                    } else {

                        if (params[2].equalsIgnoreCase("set")) {
                            if (params[3].equalsIgnoreCase("maintenance")) {
                                if (!(params[4].equalsIgnoreCase("true") || params[4].equalsIgnoreCase("false"))) {
                                    Logger.log(LoggerType.ERROR, Logger.PREFIX + "Please provide a state (boolean (true, false))");
                                    return;
                                }

                                Logger.log(LoggerType.INFO, Logger.PREFIX + "updating...");
                                boolean state = Boolean.parseBoolean(params[4]);

                                template.setMaintenance(state);
                                templateService.getTemplateSaver().save(template);

                                for (IGameServer gameServer : gameServerManager.getGameServersByTemplate(template).get()) {
                                    gameServer.update();
                                }

                                Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "updated the maintenance state of the template » " + ConsoleColors.LIGHT_BLUE + template.getName() + ConsoleColors.GRAY + "! (New state » " + ConsoleColors.LIGHT_BLUE + state + ConsoleColors.GRAY + ")");
                            } else if (params[3].equalsIgnoreCase("maxplayers")) {
                                String amountString = params[4];
                                int amount;
                                try {
                                    amount = Integer.parseInt(amountString);
                                } catch (NumberFormatException exception) {
                                    Logger.log(LoggerType.ERROR, Logger.PREFIX + "Please provide a real number (int)");
                                    return;
                                }


                                template.setMaxPlayers(Integer.parseInt(params[4]));
                                templateService.getTemplateSaver().save(template);

                                for (IGameServer gameServer : gameServerManager.getGameServersByTemplate(template).get()) {
                                    //TODO
                                    //  gameServer.sendPacket(new GameServerMaxPlayersUpdatePacket(gameServer.getTemplate().getTemplateType().equals(TemplateType.PROXY) ? "messages.getNetworkIsFull() Check TemplateCommand:117" : "messages.getServiceIsFull() Check TemplateCommand:117", gameServer.getMaxPlayers()));
                                }
                                Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "updated the maximal players of the template » " + ConsoleColors.LIGHT_BLUE + template.getName() + ConsoleColors.GRAY + "! (New state » " + ConsoleColors.LIGHT_BLUE + amount + ConsoleColors.GRAY + ")");
                                return;
                            } else {
                                Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Template-Edit]----");
                                Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "template edit <template> set maintenance <state (boolean(true, false))> " + ConsoleColors.GRAY + "to set the maintenance mode of a template");
                                Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "template edit <template> set maxplayers <amount> " + ConsoleColors.GRAY + "to set the maximal players of a template");
                                Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Template-Edit]----");
                            }
                        } else {
                            Logger.newLine();
                            Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Template-Edit]----");
                            Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "template edit <template> set maintenance <state (boolean(true, false))> " + ConsoleColors.GRAY + "to set the maintenance mode of a template");
                            Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "template edit <template> set maxplayers <amount> " + ConsoleColors.GRAY + "to set the maximal players of a template");
                            Logger.log(LoggerType.INFO, Logger.PREFIX + "----[/Template-Edit]----");
                        }
                    }
                } else {
                    sendHelp();
                }
            } else {
                sendHelp();
            }
        } catch (ExecutionException | InterruptedException exception) {
            exception.printStackTrace();
        }

    }

    private void sendHelp() {
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Template]----");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "template create " + ConsoleColors.GRAY + "to create a new template");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "template versions " + ConsoleColors.GRAY + "to show all available versions for a template");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "template info <template> " + ConsoleColors.GRAY + "to get information about a template");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "template shutdown <template> " + ConsoleColors.GRAY + "to shutdown a entire template");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "template edit <template> set maintenance <state (boolean(true, false))> " + ConsoleColors.GRAY + "to set the maintenance mode of a template");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "template edit <template> set maxplayers <amount> " + ConsoleColors.GRAY + "to set the maximal players of a template");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[/Template]----");
    }
}
