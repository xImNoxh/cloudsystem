package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.template.GameServerVersion;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.setup.CreateTemplateSetup;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

@CloudCommand.Info(
    name = "template",
    description = "template command",
    aliases = ""
)
public class TemplateCloudCommand extends CloudCommand {

    public ITemplateService templateService;

    public TemplateCloudCommand(ITemplateService templateService) {
        this.templateService = templateService;
    }

    @Override
    public void execute(String[] args) {


        if (args.length == 2 && args[1].equalsIgnoreCase("create")) {
            new CreateTemplateSetup(templateService).sendSetup();
            return;
        }

        if (args.length == 2 && args[1].equalsIgnoreCase("versions")) {
            for (GameServerVersion value : GameServerVersion.values()) {
                Logger.log(LoggerType.INFO, value.getTitle());
            }
            return;
        }

        Logger.log(LoggerType.INFO, "polo");
        Logger.log(LoggerType.INFO,Logger.PREFIX + "Use following command: "+ ConsoleColors.LIGHT_BLUE.getAnsiCode() + "template versions");
        Logger.log(LoggerType.INFO,Logger.PREFIX + "Use following command: "+ ConsoleColors.LIGHT_BLUE.getAnsiCode() + "template create");
    }
}
