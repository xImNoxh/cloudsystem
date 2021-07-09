package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.template.SimpleTemplate;

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
        if (args.length == 1) {
            System.out.println("template create <name> <minServerCount> <maxServerCount> <MINECRAFT/PROXY>");
        } else if (args.length == 6) {
            if (args[1].equalsIgnoreCase("create")) {
                String name = args[2];
                int minServerCount = Integer.parseInt(args[3]);
                int maxServerCount = Integer.parseInt(args[4]);

                ITemplate template = new SimpleTemplate(name, maxServerCount, minServerCount, TemplateType.valueOf(args[5]));

                this.templateService.getTemplateSaver().save(template);

                System.out.println("Template created!");

            }
        }
    }
}
