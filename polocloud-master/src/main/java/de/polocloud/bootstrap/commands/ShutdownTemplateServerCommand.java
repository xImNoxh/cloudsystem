package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@CloudCommand.Info(name = "shutdowntemplate", description = "shutdown all service of one template", aliases = "")
public class ShutdownTemplateServerCommand extends CloudCommand {

    private IGameServerManager gameServerManager;
    private ITemplateService templateService;

    public ShutdownTemplateServerCommand(IGameServerManager gameServerManager, ITemplateService templateService) {
        this.gameServerManager = gameServerManager;
        this.templateService = templateService;
    }

    @Override
    public void execute(String[] args) {

        if (args.length == 2) {
            ITemplate template = templateService.getTemplateByName(args[1]);
            if (template == null) {
                Logger.log(LoggerType.INFO, "This template does not exists.");
                return;
            }

            try {
                List<IGameServer> servers = gameServerManager.getGameServers().get().stream().filter(key -> key.getTemplate().equals(template)).collect(Collectors.toList());

                for (IGameServer server : servers) {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Trying to stop " + server.getName() + "...");
                    server.stop();
                }
                Logger.log(LoggerType.INFO, Logger.PREFIX + "Shutdown " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + servers.size() + ConsoleColors.GRAY.getAnsiCode() + " game servers.");

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return;
        }
        Logger.log(LoggerType.INFO, "Use following command: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "shutdowntemplate <name-id>");
    }
}
