package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
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
            System.out.println("template create <name> <minServerCount> <maxServerCount>");
        } else if (args.length == 5) {
            if (args[1].equalsIgnoreCase("create")) {
                String name = args[2];
                int minServerCount = Integer.parseInt(args[3]);
                int maxServerCount = Integer.parseInt(args[4]);

                ITemplate template = new SimpleTemplate(name, maxServerCount, minServerCount);

                this.templateService.getTemplateSaver().save(template);

                System.out.println("Template created!");

            }
        }
    }
}
