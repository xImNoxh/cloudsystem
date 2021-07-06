package de.polocloud.master.protocol.commands.executes;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CloudCommandType;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;

@CloudCommand.Info(name = "stop", description = "shutdown the cloudsystem.", aliases = {}, commandTyp = CloudCommandType.SERVICE)
public class StopCommand extends CloudCommand {

    @Override
    public void execute(String[] args) {
        Logger.log(LoggerType.INFO, "Cloud stopped!");
        System.exit(0);
    }
}
