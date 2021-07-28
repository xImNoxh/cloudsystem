package de.polocloud.hub;

import com.google.inject.Inject;
import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.hub.config.HubConfig;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@CloudCommand.Info(commandType = CommandType.INGAME, aliases = "", description = "", name = "hub")
public class HubCloudCommand extends CloudCommand {

    @Inject
    private MasterConfig config;

    private HubConfig hubConfig;

    public HubCloudCommand() {
        setAliases(CloudModule.getInstance().getHubConfig().getAliases());
        this.hubConfig = CloudModule.getInstance().getHubConfig();
    }

    @Override
    public void execute(ICommandExecutor sender, String[] args) {

        ICloudPlayer player = (ICloudPlayer) sender;

        IGameServerManager gameServerManager = Master.getInstance().getGameServerManager();
        ITemplateService templateService = Master.getInstance().getTemplateService();
        try {
            List<IGameServer> gameServersByTemplate = gameServerManager.getGameServersByTemplate(templateService.getTemplateByName(config.getProperties().getFallback()[0]).get()).get();
            IGameServer gameServer = gameServersByTemplate.stream().max(Comparator.comparingInt(IGameServer::getOnlinePlayers)).orElse(null);
            if(gameServer == null){
                player.sendMessage(hubConfig.getNoFallback());
                return;
            }

            if(gameServer.getTemplate() == player.getMinecraftServer().getTemplate()) {
                player.sendMessage(hubConfig.getAlreadyConnected());
                return;
            }

            player.sendTo(gameServer);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
