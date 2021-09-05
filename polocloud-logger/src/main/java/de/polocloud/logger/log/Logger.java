package de.polocloud.logger.log;

import jline.console.ConsoleReader;

import java.io.IOException;

public class Logger {

    private static ConsoleReader CONSOLE_READER;

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
