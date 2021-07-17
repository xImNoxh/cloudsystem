package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;

@CloudCommand.Info(
    name = "players",
    description = "manage players",
    aliases = ""
)
public class PlayersCloudCommand extends CloudCommand {

    private ICloudPlayerManager playerManager;

    public PlayersCloudCommand(ICloudPlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void execute(String[] args) {

        if (args.length >= 3) {
            if (args[1].equalsIgnoreCase("kick")) {
                String targetName = args[2];
                String message = "";
                for (int i = 3; i < args.length; i++) {
                    message += args[i] + " ";
                }

                if (playerManager.isPlayerOnline(targetName)) {
                    playerManager.getOnlinePlayer(targetName).kick(message);
                    Logger.log(LoggerType.INFO, "Der Spieler " + targetName + " wurde gekickt!");
                }else{
                    Logger.log(LoggerType.INFO, "Der Spieler " + targetName + " wurde nicht gefunden!");
                }


            }
        }

    }
}
