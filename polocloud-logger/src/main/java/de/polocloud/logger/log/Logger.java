package de.polocloud.logger.log;

import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static ConsoleReader consoleReader;

    public static void boot() {
        try {
            consoleReader = new ConsoleReader(System.in, System.out);
            new LogService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(LoggerType loggerType, String message) {
        try {
            consoleReader.println(ConsoleColors.GRAY.getAnsiCode() + message);
            consoleReader.drawLine();
            consoleReader.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogService.getLogService().getLogFileService().getLogFileWriter().write("[" + getSimpleTime() + " | " + loggerType.getLabel() + "] » " + replaceColorCodes(message));
    }

    public static void log(String message) {
        try {
            consoleReader.println(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + message);
            consoleReader.drawLine();
            consoleReader.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogService.getLogService().getLogFileService().getLogFileWriter().write(replaceColorCodes(message));
    }

    public static ConsoleReader getConsoleReader() {
        return consoleReader;
    }

    public static String replaceColorCodes(String s) {
        for (ConsoleColors value : ConsoleColors.values()) {
            s = s.replace(value.toString(), " ");
        }

        return s;
    }

    public static String getSimpleTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public static String getSimpleDate() {
        return new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    }

    public static void newLine() {
        log(" ");
    }
}
