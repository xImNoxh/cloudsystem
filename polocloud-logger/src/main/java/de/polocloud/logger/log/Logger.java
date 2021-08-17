package de.polocloud.logger.log;

import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static final String PREFIX = ConsoleColors.LIGHT_BLUE + "PoloCloud " + ConsoleColors.GRAY + "» ";
    private static ConsoleReader consoleReader;

    public static void boot() {
        try {
            consoleReader = new ConsoleReader(System.in, System.out);
            new LogService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void logErr(String message) {
        LogService.getLogService().getLogFileService().getLogFileWriter().write(replaceColorCodes("[" + getSimpleTime() + " | " + LoggerType.ERROR + "] » " + message));
    }

    public static void log(LoggerType loggerType, String message) {
        try {
            if (!loggerType.equals(LoggerType.INFO)) {
                consoleReader.println(ConsoleColors.GRAY + "[" + loggerType.getConsoleColors() + loggerType.getLabel() + ConsoleColors.GRAY + "] " + message + ConsoleColors.RESET);
            } else {
                consoleReader.println(ConsoleColors.GRAY + message + ConsoleColors.RESET);
            }
            consoleReader.drawLine();
            consoleReader.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogService.getLogService().getLogFileService().getLogFileWriter().write(replaceColorCodes("[" + getSimpleTime() + " | " + loggerType.getConsoleColors() + loggerType.getLabel() + ConsoleColors.GRAY + "] » " + message));
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
