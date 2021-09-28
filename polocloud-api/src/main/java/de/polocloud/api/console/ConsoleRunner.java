package de.polocloud.api.console;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import jline.console.ConsoleReader;

import java.io.IOException;

public class ConsoleRunner extends Thread {

    /**
     * Instance of the {@link ConsoleReader} for reading
     * the Input of the Console
     */
    private final ConsoleReader consoleReader;

    /**
     * The instance of this runner
     */
    private static ConsoleRunner instance;

    /**
     * If this runner is active and should check for commands
     */
    private boolean active;

    public ConsoleRunner() {
        instance = this;

        ConsoleReader reader = null;

        try {
            reader = new ConsoleReader(System.in, System.out);
            reader.addCompleter(new ConsoleCommandCompleter());
        } catch (IOException e) {
            e.printStackTrace();
            PoloCloudAPI.getInstance().reportException(e);
        }
        this.consoleReader = reader;
        this.active = false;
    }

    @Override
    public void run() {
        String line;

        while (isAlive()) {
            if (isActive()) {
                try {
                    this.consoleReader.setPrompt("");
                    this.consoleReader.resetPromptLine("", "", 0);
                    while ((line = this.consoleReader.readLine((PoloCloudAPI.getInstance() == null ? "" : PoloLogger.getInstance() == null ? "" : (PoloLogger.getInstance().getPrefix() == null ? "" : PoloLogger.getInstance().getPrefix())))) != null && !line.trim().isEmpty()) {
                        this.consoleReader.setPrompt("");

                        if (PoloCloudAPI.getInstance() != null && PoloCloudAPI.getInstance().getCommandManager() != null && PoloCloudAPI.getInstance().getCommandExecutor() != null) {
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static ConsoleRunner getInstance() {
        return instance;
    }

    public ConsoleReader getConsoleReader() {
        return consoleReader;
    }
}
