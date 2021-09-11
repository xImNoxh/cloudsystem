package de.polocloud.logger.log.reader;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.api.logger.helper.LogLevel;
import jline.console.ConsoleReader;

import java.io.IOException;

public class ConsoleReadThread extends Thread {

    /**
     * Instance of the {@link ConsoleReader} for reading
     * the Input of the Console
     */
    private final ConsoleReader consoleReader;

    public ConsoleReadThread(ConsoleReader consoleReader) {
        this.consoleReader = consoleReader;

        this.consoleReader.addCompleter(new CommandCompleter());

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
                while ((line = this.consoleReader.readLine((PoloCloudAPI.getInstance() == null ? "" : PoloLogger.getInstance() == null ? "" : (PoloLogger.getInstance().getPrefix() == null ? "" : PoloLogger.getInstance().getPrefix())))) != null && !line.trim().isEmpty()) {
                    this.consoleReader.setPrompt("");

                    if (PoloCloudAPI.getInstance() != null) {
                        if (!PoloCloudAPI.getInstance().getCommandManager().runCommand(line, PoloCloudAPI.getInstance().getCommandExecutor())) {
                            PoloLogger.print(LogLevel.INFO, "Unknown command... Please use the " + ConsoleColors.LIGHT_BLUE + "help " + ConsoleColors.GRAY + "command.");
                        }

                    }
                }
            } catch (IOException throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
