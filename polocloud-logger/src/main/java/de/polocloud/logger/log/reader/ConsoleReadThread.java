package de.polocloud.logger.log.reader;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.function.Consumer;

public class ConsoleReadThread extends Thread {

    private ConsoleReader consoleReader;

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
                while ((line = this.consoleReader.readLine(colorString(Logger.PREFIX))) != null && !line.trim().isEmpty()) {
                    this.consoleReader.setPrompt("");

                    String[] args = line.split(" ");

                    if(PoloCloudAPI.getInstance() != null)
                    PoloCloudAPI.getInstance().getCommandPool().getAllCachedCommands().stream().filter(key -> key.getName().equalsIgnoreCase(args[0])).forEach(key -> {
                        key.execute(args);
                    });

                }
            } catch (IOException throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public String colorString(String text) {
        for (ConsoleColors consoleColour : ConsoleColors.values())
            text = text.replace('§' + "" + consoleColour.getIndex(), consoleColour.getAnsiCode());
        return text;
    }

}
