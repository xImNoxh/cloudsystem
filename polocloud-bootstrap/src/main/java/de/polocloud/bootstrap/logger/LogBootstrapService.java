package de.polocloud.bootstrap.logger;


import de.polocloud.logger.log.LogService;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;

public class LogBootstrapService {

    private LogService logService;

    public LogBootstrapService() {
        this.logService = new LogService();
    }

    public void printSymbol(){
        Logger.log("\n" +
            "  _____      _        _____ _                 _ \n" +
            " |  __ \\    | |      / ____| |               | |\n" +
            " | |__) |__ | | ___ | |    | | ___  _   _  __| |\n" +
            " |  ___/ _ \\| |/ _ \\| |    | |/ _ \\| | | |/ _` |\n" +
            " | |  | (_) | | (_) | |____| | (_) | |_| | (_| |\n" +
            " |_|   \\___/|_|\\___/ \\_____|_|\\___/ \\__,_|\\__,_|\n" +
            "                                                \n");
        Logger.log("#This cloud was developed by " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "HttpMarco");
        Logger.log(ConsoleColors.GRAY.getAnsiCode() + "#Version of cloud - " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "v1.0.1 " +
            ConsoleColors.GRAY.getAnsiCode() + "(@Alpha) | ©opyright by PoloCloud.");
        Logger.newLine();
    }

}
