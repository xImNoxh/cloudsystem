package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.util.Snowflake;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.WrapperClient;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;

import java.util.Optional;

@CloudCommand.Info(
    name = "gameserver",
    description = "gameserver command",
    aliases = "gs"
)
public class GameServerCloudCommand extends CloudCommand {

    private Snowflake snowflake;

    private ITemplateService templateService;
    private IWrapperClientManager wrapperClientManager;

    public GameServerCloudCommand(ITemplateService templateService, IWrapperClientManager wrapperClientManager) {
        this.templateService = templateService;
        this.wrapperClientManager = wrapperClientManager;
    }


    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            Logger.log(LoggerType.INFO, "gameserver start <template>");
        } else if (args.length == 3) {
            if (args[1].equals("start")) {
                String templateName = args[2];

                ITemplate template = this.templateService.getTemplateByName(templateName);
                if (template == null) {
                    Logger.log(LoggerType.INFO, "No template founded!");
                    return;
                }

                Optional<WrapperClient> optionalWrapperClient = this.wrapperClientManager.getWrapperClients().stream().findAny();

                if (!optionalWrapperClient.isPresent()) {
                    Logger.log(LoggerType.INFO, "No wrapper founded!");
                    return;
                }

                WrapperClient wrapperClient = optionalWrapperClient.get();

                long id = snowflake.nextId();
                //wrapperClient.startServer(new SimpleGameServer(new SimpleGameServer(template.getName() + "-" + id, GameServerStatus.PENDING, null, id, template, System.currentTimeMillis())));
                Logger.log(LoggerType.INFO, "starting...");


            }
        }
    }
}
