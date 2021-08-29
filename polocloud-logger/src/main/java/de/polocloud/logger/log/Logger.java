package de.polocloud.logger.log;

import de.polocloud.api.util.PoloHelper;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.logger.log.types.LoggerType;
import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;

import java.io.IOException;

public class Logger {

    public static final String PREFIX = ConsoleColors.LIGHT_BLUE + "PoloCloud " + ConsoleColors.GRAY + "» ";
    private static ConsoleReader CONSOLE_READER;

    public static Logger INSTANCE;
    public static boolean USE_PREFIX = false;




    public void boot() {
        try {
            INSTANCE = this;
            CONSOLE_READER = new ConsoleReader(System.in, System.out);
            new LogService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(String message) {
        log(LoggerType.PREFIX, message);
    }

    public static Logger prefix() {
        USE_PREFIX = true;
        return INSTANCE;
    }

    public static void log(LoggerType loggerType, String message) {

        if (!loggerType.equals(LoggerType.INFO) && !loggerType.equals(LoggerType.PREFIX)) {
            message = "§7[" + loggerType.getConsoleColors() + loggerType.getLabel() + "§7] " + message + ConsoleColors.RESET;
        }
        if (loggerType == LoggerType.PREFIX) {
            message = PREFIX + message;
        }

        if (USE_PREFIX) {
            USE_PREFIX = false;
            message = Logger.PREFIX + message;
        }
        message = ConsoleColors.translateColorCodes('§', message);

        try {
            CONSOLE_READER.println('\r' + message);
            CONSOLE_READER.drawLine();
            CONSOLE_READER.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogService.getLogService().getLogFileService().getLogFileWriter().write(ConsoleColors.replaceColorCodes("[" + PoloHelper.getSimpleTime() + " | " + loggerType.getConsoleColors() + loggerType.getLabel() + ConsoleColors.GRAY + "] » " + message));
    }

    public static void logPrefixLess(String message) {
        message = ConsoleColors.translateColorCodes('§', message);
        try {
            CONSOLE_READER.println(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + message);
            CONSOLE_READER.drawLine();
            CONSOLE_READER.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogService.getLogService().getLogFileService().getLogFileWriter().write(ConsoleColors.replaceColorCodes(message));
    }

    public static ConsoleReader getConsoleReader() {
        return CONSOLE_READER;
    }


    public static void newLine() {
        logPrefixLess(" ");
    }
}
