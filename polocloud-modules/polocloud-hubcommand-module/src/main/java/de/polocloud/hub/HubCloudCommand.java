package de.polocloud.hub;

import com.google.inject.Inject;
import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.hub.config.HubConfig;

import java.util.Comparator;
import java.util.List;

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

        //Check if the Fallback List in the Config ist empty
        if (config.getProperties().getFallbackProperties().isEmpty()) {
            player.sendMessage(hubConfig.getNoService());
            return;
        }


        List<IGameServer> chosenFallbackServers = Master.getInstance().getFallbackSearchService().searchForTemplate(player, true);

        //Check if no template was found
        if (chosenFallbackServers == null) {
            player.sendMessage(hubConfig.getNoService());
            return;
        }

        IGameServer gameServer = chosenFallbackServers.stream().max(Comparator.comparingInt(IGameServer::getOnlinePlayers)).orElse(null);
        if (gameServer == null) {
            player.sendMessage(hubConfig.getNoService());
            return;
        }

        if (gameServer.getTemplate() == player.getMinecraftServer().getTemplate()) {
            player.sendMessage(hubConfig.getAlreadyConnected());
            return;
        }

        player.sendTo(gameServer);
    }
}
