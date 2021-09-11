package de.polocloud.logger.log;

import jline.console.ConsoleReader;

import java.io.IOException;

public class Logger {

    /**
     * ConsoleReader instance, for reading the input of the console
     */
    private static ConsoleReader CONSOLE_READER;

    /**
     * Boots up the CONSOLE_READ and creating a new instance of the {@link LogService}
     */
    public void boot() {
        try {
            CONSOLE_READER = new ConsoleReader(System.in, System.out);
            new LogService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConsoleReader getConsoleReader() {
        return CONSOLE_READER;
    }

}
