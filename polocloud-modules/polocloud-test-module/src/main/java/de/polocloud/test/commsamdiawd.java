package de.polocloud.test;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.player.ICloudPlayer;

@CloudCommand.Info(commandType = CommandType.INGAME_CONSOLE, aliases = "", description = "",name = "polo123")
public class commsamdiawd extends CloudCommand {

    @Override
    public void execute(ICommandExecutor sender, String[] args) {

        ICloudPlayer player = (ICloudPlayer)sender;

        player.sendMessage("§1testadwadawdwa§edw§ca");
    }
}
