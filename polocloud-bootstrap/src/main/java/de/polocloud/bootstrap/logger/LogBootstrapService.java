package de.polocloud.bootstrap.logger;

import de.polocloud.api.APIVersion;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.logger.log.LogService;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.reader.ConsoleReadThread;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.api.logger.helper.LogLevel;

import java.io.FileReader;
import java.util.Arrays;

public class LogBootstrapService {

    /**
     * Prints out the PoloCloud Header on Wrapper or Master startup
     */
    public void printSymbol() {

        new ConsoleReadThread(Logger.getConsoleReader());

        APIVersion version = PoloCloudAPI.class.getAnnotation(APIVersion.class);

        PoloLogger.print(LogLevel.INFO, "\n" +
            "  _____      _        _____ _                 _ \n" +
            " |  __ \\    | |      / ____| |               | |\n" +
            " | |__) |__ | | ___ | |    | | ___  _   _  __| |\n" +
            " |  ___/ _ \\| |/ _ \\| |    | |/ _ \\| | | |/ _` |\n" +
            " | |  | (_) | | (_) | |____| | (_) | |_| | (_| |\n" +
            " |_|   \\___/|_|\\___/ \\_____|_|\\___/ \\__,_|\\__,_|\n" +
            "                                                \n");
        PoloLogger.print(LogLevel.INFO, "#This cloud was developed by " + ConsoleColors.LIGHT_BLUE + Arrays.toString(version.developers()).replace("[", "").replace("]", ""));
        PoloLogger.print(LogLevel.INFO, ConsoleColors.GRAY + "#Version of cloud - " + ConsoleColors.LIGHT_BLUE + version.version() + ConsoleColors.GRAY + " (" + version.identifier() + ") | ©opyright by PoloCloud.");
        PoloLogger.print(LogLevel.INFO, "Problems or questions? Our Discord » " + ConsoleColors.LIGHT_BLUE + version.discord());
        PoloLogger.print(LogLevel.INFO, "");
    }
}
