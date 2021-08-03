package de.polocloud.bootstrap.commands.version2;

import com.google.inject.Inject;
import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRequestShutdownPacket;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.WrapperClient;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

@CloudCommand.Info(name = "wrapper", description = "Manage a wrapper", aliases = "", commandType = CommandType.CONSOLE)
public class WrapperCommand extends CloudCommand {

    @Inject
    private IWrapperClientManager wrapperClientManager;

    public WrapperCommand() {
    }

    @Override
    public void execute(ICommandExecutor sender, String[] args) {
        if (args.length == 3) {
            if (args[1].equalsIgnoreCase("stop") || args[1].equalsIgnoreCase("shutdown")) {
                String wrapperName = args[2];
                WrapperClient wrapperClient = wrapperClientManager.getWrapperClientByName(wrapperName);
                if (wrapperClient == null) {
                    Logger.log(LoggerType.WARNING, Logger.PREFIX + "The wrapper » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + wrapperName + ConsoleColors.GRAY.getAnsiCode() + " isn't connected!");
                    return;
                }
                wrapperName = wrapperClient.getName();
                Logger.log(LoggerType.INFO, Logger.PREFIX + "Requesting shutdown...");
                wrapperClient.sendPacket(new WrapperRequestShutdownPacket());
                Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN.getAnsiCode() + "Successfully " + ConsoleColors.GRAY.getAnsiCode() + "request shutdown of wrapper » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + wrapperName + ConsoleColors.GRAY.getAnsiCode() + "!");
            } else if (args[1].equalsIgnoreCase("info")) {
                Logger.log(LoggerType.INFO, Logger.PREFIX + "Stay tuned?!");
            } else {
                sendHelp();
            }
        } else {
            sendHelp();
        }
    }

    private void sendHelp() {
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Wrapper]----");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "wrapper stop/shutdown <wrapper> " + ConsoleColors.GRAY.getAnsiCode() + "to shutdown a wrapper");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "gameserver info <server> " + ConsoleColors.GRAY.getAnsiCode() + "to get information of a gameserver");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[/Wrapper]----");
    }
}
