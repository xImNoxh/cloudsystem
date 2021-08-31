package de.polocloud.bootstrap.logger;


import com.google.gson.JsonObject;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.logger.log.LogService;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.reader.ConsoleReadThread;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.api.logger.helper.LogLevel;

import java.io.FileReader;

public class LogBootstrapService {

    private LogService logService;

    public LogBootstrapService() {
        this.logService = new LogService();
    }

    /**
     * Prints out the PoloCloud Header on Wrapper or Master startup
     */
    public void printSymbol() {

        new ConsoleReadThread(Logger.getConsoleReader());

        String currentVersion = "No version fetched";
        try {
            FileReader reader = new FileReader("launcher.json");
            currentVersion = PoloHelper.GSON_INSTANCE.fromJson(reader, JsonObject.class).get("version").getAsString();
            reader.close();
        } catch (Exception ignored) {
        }

        Logger.logPrefixLess("\n" +
            "  _____      _        _____ _                 _ \n" +
            " |  __ \\    | |      / ____| |               | |\n" +
            " | |__) |__ | | ___ | |    | | ___  _   _  __| |\n" +
            " |  ___/ _ \\| |/ _ \\| |    | |/ _ \\| | | |/ _` |\n" +
            " | |  | (_) | | (_) | |____| | (_) | |_| | (_| |\n" +
            " |_|   \\___/|_|\\___/ \\_____|_|\\___/ \\__,_|\\__,_|\n" +
            "                                                \n");
        PoloLogger.print(LogLevel.INFO, "#This cloud was developed by " + ConsoleColors.LIGHT_BLUE + "HttpMarco, Max_DE");
        PoloLogger.print(LogLevel.INFO, ConsoleColors.GRAY + "#Version of cloud - " + ConsoleColors.LIGHT_BLUE + currentVersion +
            ConsoleColors.GRAY + " (@Alpha) | ©opyright by PoloCloud.");
        PoloLogger.print(LogLevel.INFO, "Problems or questions? Our Discord » " + ConsoleColors.LIGHT_BLUE + "https://discord.gg/HyRnsdkUBA");
        Logger.newLine();
    }
}
