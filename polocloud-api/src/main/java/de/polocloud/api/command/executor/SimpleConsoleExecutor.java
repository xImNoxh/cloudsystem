package de.polocloud.api.command.executor;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.console.ConsoleColors;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;

public class SimpleConsoleExecutor implements ConsoleExecutor {

    @Override
    public void runCommand(String command) {
        PoloCloudAPI.getInstance().getCommandManager().runCommand(command, this);
    }

    @Override
    public void sendMessage(String text) {
        PoloLogger.getInstance().log(LogLevel.INFO, ConsoleColors.translateColorCodes('§', text));
    }

    @Override
    public ExecutorType getType() {
        return ExecutorType.CONSOLE;
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }
}
