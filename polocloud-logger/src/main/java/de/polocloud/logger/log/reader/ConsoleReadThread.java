package de.polocloud.logger.log.reader;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import jline.console.ConsoleReader;

import java.io.IOException;

public class ConsoleReadThread extends Thread {

    private final ConsoleReader consoleReader;

    public ConsoleReadThread(ConsoleReader consoleReader) {
        this.consoleReader = consoleReader;

        this.setDaemon(true);
        this.setPriority(Thread.MIN_PRIORITY);
        this.start();
    }

    @Override
    public void run() {
        String line;
        while (!isInterrupted()) {
            try {
                this.consoleReader.setPrompt("");
                this.consoleReader.resetPromptLine("", "", 0);
                while ((line = this.consoleReader.readLine(Logger.PREFIX)) != null && !line.trim().isEmpty()) {
                    this.consoleReader.setPrompt("");

                    if (PoloCloudAPI.getInstance() != null) {
                        if (!PoloCloudAPI.getInstance().getCommandManager().runCommand(line, PoloCloudAPI.getInstance().getCommandExecutor())) {
                            Logger.log(LoggerType.INFO, Logger.PREFIX + "Unknown command... Please use the " + ConsoleColors.LIGHT_BLUE + "help " + ConsoleColors.GRAY + "command.");
                        }

                    }
                }
            } catch (IOException throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
