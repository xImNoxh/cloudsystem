package de.polocloud.bootstrap.logger;


import com.google.gson.JsonObject;
import de.polocloud.api.util.PoloUtils;
import de.polocloud.logger.log.LogService;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.reader.ConsoleReadThread;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

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
            currentVersion = PoloUtils.GSON_INSTANCE.fromJson(reader, JsonObject.class).get("version").getAsString();
            reader.close();
        } catch (Exception ignored) {
        }

        Logger.log("\n" +
            "  _____      _        _____ _                 _ \n" +
            " |  __ \\    | |      / ____| |               | |\n" +
            " | |__) |__ | | ___ | |    | | ___  _   _  __| |\n" +
            " |  ___/ _ \\| |/ _ \\| |    | |/ _ \\| | | |/ _` |\n" +
            " | |  | (_) | | (_) | |____| | (_) | |_| | (_| |\n" +
            " |_|   \\___/|_|\\___/ \\_____|_|\\___/ \\__,_|\\__,_|\n" +
            "                                                \n");
        Logger.log(LoggerType.INFO, "#This cloud was developed by " + ConsoleColors.LIGHT_BLUE + "HttpMarco, Max_DE");
        Logger.log(LoggerType.INFO, ConsoleColors.GRAY + "#Version of cloud - " + ConsoleColors.LIGHT_BLUE + currentVersion +
            ConsoleColors.GRAY + " (@Alpha) | ©opyright by PoloCloud.");
        Logger.log(LoggerType.INFO, "Problems or questions? Our Discord » " + ConsoleColors.LIGHT_BLUE + "https://discord.gg/HyRnsdkUBA");
        Logger.newLine();
    }
}
