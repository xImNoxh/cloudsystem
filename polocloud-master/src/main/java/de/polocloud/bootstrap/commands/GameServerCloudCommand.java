package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.WrapperClient;

import java.util.Optional;

@CloudCommand.Info(
    name = "gameserver",
    description = "gameserver command",
    aliases = "gs"
)
public class GameServerCloudCommand extends CloudCommand {

    private ITemplateService templateService;
    private IWrapperClientManager wrapperClientManager;

    public GameServerCloudCommand(ITemplateService templateService, IWrapperClientManager wrapperClientManager) {
        this.templateService = templateService;
        this.wrapperClientManager = wrapperClientManager;
    }


    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            System.out.println("gameserver start <template>");
        } else if (args.length == 3) {
            if (args[1].equals("start")) {
                String templateName = args[2];

                ITemplate template = this.templateService.getTemplateByName(templateName);
                if (template == null) {
                    System.out.println("Template nicht gefunden");
                    return;
                }

                Optional<WrapperClient> optionalWrapperClient = this.wrapperClientManager.getWrapperClients().stream().findAny();

                if (!optionalWrapperClient.isPresent()) {
                    System.out.println("Kein Wrapper gefunden!");
                    return;
                }

                WrapperClient wrapperClient = optionalWrapperClient.get();
                wrapperClient.startServer(template);
                System.out.println("starting...");


            }
        }
    }
}
