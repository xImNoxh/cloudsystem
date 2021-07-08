package de.polocloud.logger.log.reader;

import de.polocloud.api.CloudAPI;
import de.polocloud.logger.log.types.ConsoleColors;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.function.Consumer;

public class ConsoleReadThread extends Thread {

    private ConsoleReader consoleReader;
    private static Consumer<String> INPUT;

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
                while ((line = this.consoleReader.readLine(colorString("§b" + "PoloCloud" + " §7» "))) != null && !line.trim().isEmpty()) {
                    this.consoleReader.setPrompt("");
                    String finalLine = line;



                    CloudAPI.getInstance().getCommandPool().getAllCachedCommands().stream().filter(key ->
                            key.getName().equalsIgnoreCase(finalLine.split(" ")[0])).forEach(commands -> commands.execute(finalLine.split(" ")));
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
