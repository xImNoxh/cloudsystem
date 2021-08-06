package de.polocloud.bootstrap.logger;


import de.polocloud.logger.log.LogService;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.reader.ConsoleReadThread;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogBootstrapService {

    private LogService logService;

    public LogBootstrapService() {
        this.logService = new LogService();
    }

    public void printSymbol(){

        new ConsoleReadThread(Logger.getConsoleReader());

        Logger.log("\n" +
            "  _____      _        _____ _                 _ \n" +
            " |  __ \\    | |      / ____| |               | |\n" +
            " | |__) |__ | | ___ | |    | | ___  _   _  __| |\n" +
            " |  ___/ _ \\| |/ _ \\| |    | |/ _ \\| | | |/ _` |\n" +
            " | |  | (_) | | (_) | |____| | (_) | |_| | (_| |\n" +
            " |_|   \\___/|_|\\___/ \\_____|_|\\___/ \\__,_|\\__,_|\n" +
            "                                                \n");
        Logger.log(LoggerType.INFO, "#This cloud was developed by " + ConsoleColors.LIGHT_BLUE + "HttpMarco, Max_DE");
        Logger.log(LoggerType.INFO,  ConsoleColors.GRAY + "#Version of cloud - " + ConsoleColors.LIGHT_BLUE + "v1.0.1 " +
            ConsoleColors.GRAY + "(@Alpha) | ©opyright by PoloCloud.");
        Logger.log(LoggerType.INFO, "Problems or questions? Our Discord » " + ConsoleColors.LIGHT_BLUE + "https://discord.gg/HyRnsdkUBA");
        Logger.newLine();
    }
}
