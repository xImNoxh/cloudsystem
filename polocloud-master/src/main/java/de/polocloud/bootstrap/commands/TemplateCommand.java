package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerMaintenanceUpdatePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerMaxPlayersUpdatePacket;
import de.polocloud.api.template.GameServerVersion;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.setup.CreateTemplateSetup;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.util.List;
import java.util.concurrent.ExecutionException;

@CloudCommand.Info(name = "template", description = "Manage a template", aliases = "", commandType = CommandType.CONSOLE)
public class TemplateCommand extends CloudCommand {

    private ITemplateService templateService;
    private IGameServerManager gameServerManager;

    public TemplateCommand(ITemplateService templateService, IGameServerManager gameServerManager) {
        this.templateService = templateService;
        this.gameServerManager = gameServerManager;
    }

    @Override
    public void execute(ICommandExecutor sender, String[] args) {
        try {
            if (args.length == 2) {
                if (args[1].equalsIgnoreCase("create")) {
                    new CreateTemplateSetup(templateService).sendSetup();
                    return;
                } else if (args[1].equalsIgnoreCase("versions")) {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Available Versions » ");
                    Logger.newLine();
                    for (GameServerVersion value : GameServerVersion.values()) {
                        Logger.log(LoggerType.INFO, value.getTitle());
                    }
                    return;
                } else {
                    sendHelp();
                }
            } else if (args.length == 3) {
                if (args[1].equalsIgnoreCase("shutdown") || args[1].equalsIgnoreCase("stop")) {
                    String templateName = args[2];
                    ITemplate template = templateService.getTemplateByName(templateName).get();
                    if (template == null) {
                        Logger.log(LoggerType.WARNING, Logger.PREFIX + "The template » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + templateName + ConsoleColors.GRAY.getAnsiCode() + " doesn't exists!");
                    } else {
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "Stopping template » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + template.getName() + ConsoleColors.GRAY.getAnsiCode() + "...");
                        List<IGameServer> gameServersInTemplate = gameServerManager.getGameServersByTemplate(template).get();
                        int size = gameServersInTemplate.size();
                        for (IGameServer gameServer : gameServersInTemplate) {
                            gameServer.stop();
                        }
                        Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN.getAnsiCode() + "Successfully " + ConsoleColors.GRAY.getAnsiCode() + "stopped " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + size + ConsoleColors.GRAY.getAnsiCode() + " servers of template » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + template.getName() + ConsoleColors.GRAY.getAnsiCode() + "!");
                    }
                    return;
                } else if (args[1].equalsIgnoreCase("info")) {
                    String templateName = args[2];
                    ITemplate template = templateService.getTemplateByName(templateName).get();
                    if (template == null) {
                        Logger.log(LoggerType.WARNING, Logger.PREFIX + "The template » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + templateName + ConsoleColors.GRAY.getAnsiCode() + " doesn't exists!");
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
            } else if (args.length == 6) {
                if (args[1].equalsIgnoreCase("edit")) {
                    String templateName = args[2];
                    ITemplate template = templateService.getTemplateByName(templateName).get();
                    if (template == null) {
                        Logger.log(LoggerType.WARNING, Logger.PREFIX + "The template » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + templateName + ConsoleColors.GRAY.getAnsiCode() + " doesn't exists!");
                    } else {

                        if (args[3].equalsIgnoreCase("set")) {
                            if (args[4].equalsIgnoreCase("maintenance")) {
                                if (!(args[5].equalsIgnoreCase("true") || args[5].equalsIgnoreCase("false"))) {
                                    Logger.log(LoggerType.ERROR, Logger.PREFIX + "Please provide a state (boolean (true, false))");
                                    return;
                                }

                                Logger.log(LoggerType.INFO, Logger.PREFIX + "updating...");
                                boolean state = Boolean.parseBoolean(args[5]);

                                template.setMaintenance(state);
                                templateService.getTemplateSaver().save(template);

                                //TODO messages
                                for (IGameServer gameServer : gameServerManager.getGameServersByTemplate(template).get()) {
                                    gameServer.sendPacket(new GameServerMaintenanceUpdatePacket(template.isMaintenance(),
                                        gameServer.getTemplate().getTemplateType() == TemplateType.PROXY ?
                                            "messages.getProxyMaintenanceMessage() Check TemplateCommand:98" : "messages.getGroupMaintenanceMessage() Check TemplateCommand:98"));
                                }

                                Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN.getAnsiCode() + "Successfully " + ConsoleColors.GRAY.getAnsiCode() + "updated the maintenance state of the template » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + template.getName() + ConsoleColors.GRAY.getAnsiCode() + "! (New state » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + state + ConsoleColors.GRAY.getAnsiCode() + ")");
                            } else if (args[4].equalsIgnoreCase("maxplayers")) {
                                String amountString = args[5];
                                int amount;
                                try {
                                    amount = Integer.parseInt(amountString);
                                } catch (NumberFormatException exception) {
                                    Logger.log(LoggerType.ERROR, Logger.PREFIX + "Please provide a real number (int)");
                                    return;
                                }


                                template.setMaxPlayers(Integer.parseInt(args[5]));
                                templateService.getTemplateSaver().save(template);

                                for (IGameServer gameServer : gameServerManager.getGameServersByTemplate(template).get()) {
                                    gameServer.sendPacket(new GameServerMaxPlayersUpdatePacket(gameServer.getTemplate().getTemplateType().equals(TemplateType.PROXY) ? "messages.getNetworkIsFull() Check TemplateCommand:117" : "messages.getServiceIsFull() Check TemplateCommand:117", gameServer.getMaxPlayers()));
                                }
                                Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN.getAnsiCode() + "Successfully " + ConsoleColors.GRAY.getAnsiCode() + "updated the maximal players of the template » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + template.getName() + ConsoleColors.GRAY.getAnsiCode() + "! (New state » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + amount + ConsoleColors.GRAY.getAnsiCode() + ")");
                                return;
                            } else {
                                Logger.newLine();
                                Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Template-Edit]----");
                                Logger.newLine();
                                Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "template edit <template> set maintenance <state (boolean(true, false))> " + ConsoleColors.GRAY.getAnsiCode() + "to set the maintenance mode of a template");
                                Logger.newLine();
                                Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "template edit <template> set maxplayers <amount> " + ConsoleColors.GRAY.getAnsiCode() + "to set the maximal players of a template");
                                Logger.newLine();
                                Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Template-Edit]----");
                            }
                        } else {
                            Logger.newLine();
                            Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Template-Edit]----");
                            Logger.newLine();
                            Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "template edit <template> set maintenance <state (boolean(true, false))> " + ConsoleColors.GRAY.getAnsiCode() + "to set the maintenance mode of a template");
                            Logger.newLine();
                            Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "template edit <template> set maxplayers <amount> " + ConsoleColors.GRAY.getAnsiCode() + "to set the maximal players of a template");
                            Logger.newLine();
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
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Template]----");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "template create " + ConsoleColors.GRAY.getAnsiCode() + "to create a new template");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "template versions " + ConsoleColors.GRAY.getAnsiCode() + "to show all available versions for a template");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "template info <template> " + ConsoleColors.GRAY.getAnsiCode() + "to get information about a template");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "template shutdown <template> " + ConsoleColors.GRAY.getAnsiCode() + "to shutdown a entire template");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "template edit <template> set maintenance <state (boolean(true, false))> " + ConsoleColors.GRAY.getAnsiCode() + "to set the maintenance mode of a template");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "template edit <template> set maxplayers <amount> " + ConsoleColors.GRAY.getAnsiCode() + "to set the maximal players of a template");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[/Template]----");
    }
}
