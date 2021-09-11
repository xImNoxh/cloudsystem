package de.polocloud.bootstrap.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Arguments;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.command.identifier.TabCompletable;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.template.helper.GameServerVersion;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.ITemplateManager;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.bootstrap.setup.CreateTemplateSetup;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.api.logger.helper.LogLevel;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TemplateCommand implements CommandListener, TabCompletable {

    private ITemplateManager templateService;
    private IGameServerManager gameServerManager;

    public TemplateCommand(ITemplateManager templateService, IGameServerManager gameServerManager) {
        this.templateService = templateService;
        this.gameServerManager = gameServerManager;
    }

    @Command(name = "template", description = "Manage a template", aliases = "group")
    public void execute(CommandExecutor sender, String[] fullArgs, @Arguments(min = 1, max = 7, message = {"----[Template]----", "Use §btemplate create §7to create a new template", "Use §btemplate versions §7to show all available versions for a template", "Use §btemplate info <template> §7to get information about a template", "Use §btemplate shutdown <template> §7to shutdown a entire template", "Use §btemplate edit <template> set maintenance <state (boolean(true, false))> §7to set the maintenance mode of a template", "Use §btemplate edit <template> set maxplayers <amount> §7to set the maximal players of a template", "----[/Template]----"}) String... params) {
        if(params.length == 1 && params[0].equalsIgnoreCase("create")){
            new CreateTemplateSetup(templateService).sendSetup();
            PoloCloudAPI.getInstance().reload();
        }else if(params.length == 1 && params[0].equalsIgnoreCase("versions")){
            PoloLogger.print(LogLevel.INFO, "Available Versions » ");
            for (GameServerVersion value : GameServerVersion.values()) {
                PoloLogger.print(LogLevel.INFO, "§b" + value.getTitle() + " §7(§b" + value.getTemplateType().getDisplayName() + "§7)");
            }
        }else if(params.length == 2 && (params[0].equalsIgnoreCase("shutdown") || params[0].equalsIgnoreCase("stop"))){
            String templateName = params[1];
            ITemplate template = templateService.getTemplate(templateName);
            if (template == null) {
                PoloLogger.print(LogLevel.WARNING, "§7The template » §b" + templateName + "");
                PoloLogger.print(LogLevel.WARNING, "The template » " + ConsoleColors.LIGHT_BLUE + templateName + ConsoleColors.GRAY + " doesn't exists!");
            } else {
                PoloLogger.print(LogLevel.INFO, "Stopping template » " + ConsoleColors.LIGHT_BLUE + template.getName() + ConsoleColors.GRAY + "...");
                List<IGameServer> gameServersInTemplate = gameServerManager.getAllCached(template);
                int size = gameServersInTemplate.size();
                for (IGameServer gameServer : gameServersInTemplate) {
                    gameServer.terminate();
                }
                PoloLogger.print(LogLevel.INFO, ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "stopped " + ConsoleColors.LIGHT_BLUE + size + ConsoleColors.GRAY + " servers of template » " + ConsoleColors.LIGHT_BLUE + template.getName() + ConsoleColors.GRAY + "!");
            }
        }else if(params.length == 2 && params[0].equalsIgnoreCase("info")){
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
        } else if(params.length == 5 && params[0].equalsIgnoreCase("edit")){
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
                        template.update();
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
                        template.update();

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

    @Override
    public List<String> onTabComplete(CommandExecutor executor, String[] args) {
        if (args.length == 0) {
            return Arrays.asList("create", "versions", "info", "shutdown", "edit");
        }else if(args.length == 1){
            switch (args[0]) {
                case "info":
                case "shutdown":
                case "edit": {
                    List<String> strings = new LinkedList<>();
                    for (ITemplate template : PoloCloudAPI.getInstance().getTemplateManager().getTemplates()) {
                        strings.add(template.getName());
                    }
                    return strings;
                }
            }
        }else if(args.length == 2 && args[0].equals("edit")){
            return Collections.singletonList("set");
        }else if(args.length == 3 && (args[0].equals("edit") && args[2].equals("set"))){
            return Arrays.asList("maintenance", "maxplayers");
        }else if(args.length == 4 && (args[0].equals("edit") && args[2].equals("set") && args[3].equals("maintenance"))){
            return Arrays.asList("true", "false");
        }
        return new LinkedList<>();
    }
}
