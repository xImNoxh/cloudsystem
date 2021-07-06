package de.polocloud.master.bootstrap;

import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.reader.ConsoleReadThread;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.master.CloudAPIImpl;

public class Bootstrap {

    public static void main(String[] args) {
        Logger.boot();
        Logger.log("\n" +
                "  _____      _        _____ _                 _ \n" +
                " |  __ \\    | |      / ____| |               | |\n" +
                " | |__) |__ | | ___ | |    | | ___  _   _  __| |\n" +
                " |  ___/ _ \\| |/ _ \\| |    | |/ _ \\| | | |/ _` |\n" +
                " | |  | (_) | | (_) | |____| | (_) | |_| | (_| |\n" +
                " |_|   \\___/|_|\\___/ \\_____|_|\\___/ \\__,_|\\__,_|\n");
        Logger.newLine();
        Logger.log("#This cloud was developed by " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "HttpMarco" + ConsoleColors.GRAY.getAnsiCode() + ".");
        Logger.log(ConsoleColors.GRAY.getAnsiCode() + "#Version of cloud - " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "v1.0.1 " + ConsoleColors.GRAY.getAnsiCode() + "(@Alpha) | ©opyright by PoloCloud.");
        Logger.newLine();

        new ConsoleReadThread(Logger.getConsoleReader());
        new CloudAPIImpl();
    }
}
