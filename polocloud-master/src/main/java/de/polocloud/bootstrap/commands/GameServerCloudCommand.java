package de.polocloud.bootstrap.commands;

import com.google.inject.Inject;
import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.util.Snowflake;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.WrapperClient;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@CloudCommand.Info(
    name = "gameserver",
    description = "gameserver command",
    aliases = "gs", commandType = CommandType.CONSOLE
)
public class GameServerCloudCommand extends CloudCommand {

    @Inject
    private Snowflake snowflake;

    @Inject
    private ITemplateService templateService;

    @Inject
    private IWrapperClientManager wrapperClientManager;

    @Inject
    private IGameServerManager gameServerManager;

    public GameServerCloudCommand() {

    }

    public GameServerCloudCommand(ITemplateService templateService, IWrapperClientManager wrapperClientManager) {
        this.templateService = templateService;
        this.wrapperClientManager = wrapperClientManager;
    }


    @Override
    public void execute(ICommandExecutor commandSender, String[] args) {
        if (args.length == 1) {
            Logger.log(LoggerType.INFO, "gameserver start <template>");
        } else if (args.length == 3) {
            if (args[1].equals("start")) {
                String templateName = args[2];

                ITemplate template = null;
                try {
                    template = this.templateService.getTemplateByName(templateName).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                if (template == null) {
                    Logger.log(LoggerType.INFO, "No template found!");
                    return;
                }

                Optional<WrapperClient> optionalWrapperClient = this.wrapperClientManager.getWrapperClients().stream().findAny();

                if (!optionalWrapperClient.isPresent()) {
                    Logger.log(LoggerType.INFO, "No wrapper connected!");
                    return;
                }

                WrapperClient wrapperClient = optionalWrapperClient.get();

                long id = snowflake.nextId();
                SimpleGameServer gameServer = new SimpleGameServer(template.getName() + "-" + generateServerId(template),
                    GameServerStatus.PENDING, null, id, template, System.currentTimeMillis(), template.getMotd(), template.getMaxPlayers());
                gameServerManager.registerGameServer(gameServer);
                wrapperClient.startServer(gameServer);
                Logger.log(LoggerType.INFO, Logger.PREFIX + "starting...");
            }
        }
    }

    private int generateServerId(ITemplate template) {
        int currentId = 1;

        boolean found = false;

        while (!found) {

            try {
                IGameServer gameServerByName = gameServerManager.getGameServerByName(template.getName() + "-" + currentId).get();

                if (gameServerByName == null) {
                    found = true;
                } else {
                    currentId++;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return currentId;
    }

}
