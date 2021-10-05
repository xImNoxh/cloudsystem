package de.polocloud.wrapper.version;

import de.polocloud.api.config.FileConstants;
import de.polocloud.api.console.ConsoleColors;
import de.polocloud.api.gameserver.helper.GameServerVersion;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.util.PoloHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * This class was developed by DasLixou :3
 * */

public class VersionInstaller {

    public static void installVersion(GameServerVersion version) {
        PoloLogger.print(LogLevel.INFO, "§7Downloading §b" + version.getTemplateType().getDisplayName() + "-Version§7... (§3" + version.getTitle() + "§7)");

        // TODO: NEED HERE TO DOWNLOAD THE JAR AND PUT IT INTO PATCH REGION
        File patchServer = new File(FileConstants.WRAPPER_PATCHER_FOLDER, version.getTitle());
        patchServer.mkdirs();
        try {
            FileUtils.copyURLToFile(new URL(version.getUrl()), new File(patchServer, version.getTitle() + ".jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        PoloLogger.print(LogLevel.INFO, "Downloading of (§3" + version.getTitle() + "§7)" + ConsoleColors.GREEN + " successfully " + ConsoleColors.GRAY + "completed.");
        if(version.isPatchable()) {
            PoloLogger.print(LogLevel.INFO, "§7Patching §b" + version.getTemplateType().getDisplayName() + "-Version§7... (§3" + version.getTitle() + "§7)");

            PoloLogger.print(LogLevel.INFO, "Patch of (§3" + version.getTitle() + "§7)" + ConsoleColors.GREEN + " successfully " + ConsoleColors.GRAY + "completed.");
        }

        File serverFile = new File(FileConstants.WRAPPER_STORAGE_VERSIONS, version.getTitle() + ".jar");
        serverFile.getParentFile().mkdirs();
        try {
            FileUtils.copyURLToFile(new URL(version.getUrl()), serverFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PoloHelper.deleteFolder(patchServer);
    }

}
