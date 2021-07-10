package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.template.GameServerVersion;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.template.SimpleTemplate;
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
        if(args.length == 7) {
            if (args[1].equalsIgnoreCase("create")) {
                String name = args[2];
                int minServerCount = Integer.parseInt(args[3]);
                int maxServerCount = Integer.parseInt(args[4]);

                ITemplate template = new SimpleTemplate(name, maxServerCount, minServerCount, TemplateType.valueOf(args[5]), GameServerVersion.getVersion(args[6]));

                this.templateService.getTemplateSaver().save(template);
                Logger.log(LoggerType.INFO, "Template created.");
                return;
            }
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("versions")) {
                for (GameServerVersion value : GameServerVersion.values()) {
                    Logger.log(LoggerType.INFO, value.getTitle());
                }
                return;
            }
        }
        Logger.log(LoggerType.INFO,"Use following command: "+ ConsoleColors.LIGHT_BLUE.getAnsiCode() + "template versions");
        Logger.log(LoggerType.INFO,"Use following command: "+ ConsoleColors.LIGHT_BLUE.getAnsiCode() + "template create <name> <minServerCount> <maxServerCount> <MINECRAFT/PROXY> <version>");
    }
}
