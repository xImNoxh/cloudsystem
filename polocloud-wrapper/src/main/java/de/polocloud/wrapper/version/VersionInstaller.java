package de.polocloud.wrapper.version;

import de.polocloud.api.config.FileConstants;
import de.polocloud.api.console.ConsoleColors;
import de.polocloud.api.gameserver.helper.GameServerVersion;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * This class was developed by DasLixou :3
 * */

public class VersionInstaller {

    public static void installVersion(GameServerVersion version) {
        PoloLogger.print(LogLevel.INFO, "§7Downloading §bVersion §e" + version.getTitle() + "§7...");
        // TODO: NEED HERE TO DOWNLOAD THE JAR AND PUT IT INTO PATCH REGION
        PoloLogger.print(LogLevel.INFO, "§7Download of §bVersion §e" + version.getTitle() + ConsoleColors.GREEN + " successfully " + ConsoleColors.GRAY + "completed.");
        if(version.isPatchable()) {
            PoloLogger.print(LogLevel.INFO, "§7Patching §bVersion §e" + version.getTitle() + "§7...");

            PoloLogger.print(LogLevel.INFO, "§7Patch of §bVersion §e" + version.getTitle() + ConsoleColors.GREEN + " successfully " + ConsoleColors.GRAY + "completed.");
        }
        File serverFile = new File(FileConstants.WRAPPER_STORAGE_VERSIONS, version.getTitle() + ".jar");
        serverFile.getParentFile().mkdirs();
        try {
            FileUtils.copyURLToFile(new URL(version.getUrl()), serverFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
