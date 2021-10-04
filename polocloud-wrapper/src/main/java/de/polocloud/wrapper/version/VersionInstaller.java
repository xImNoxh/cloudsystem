package de.polocloud.wrapper.version;

import de.polocloud.api.console.ConsoleColors;
import de.polocloud.api.gameserver.helper.GameServerVersion;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;

/**
 * This class was developed by DasLixou :3
 * */

public class VersionInstaller {

    public static void installVersion(GameServerVersion version) {
        PoloLogger.print(LogLevel.INFO, "§7Downloading §bVersion §e" + version.getTitle() + "§7...");
        /*serverFile.getParentFile().mkdirs();
        try {
            FileUtils.copyURLToFile(new URL(template.getVersion().getUrl()), serverFile);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        PoloLogger.print(LogLevel.INFO, "§7Download of §bVersion §e" + version.getTitle() + ConsoleColors.GREEN + " successfully " + ConsoleColors.GRAY + "completed.");
        if(version.isPatchable()) {
            PoloLogger.print(LogLevel.INFO, "§7Patching §bVersion §e" + version.getTitle() + "§7...");

            PoloLogger.print(LogLevel.INFO, "§7Patch of §bVersion §e" + version.getTitle() + ConsoleColors.GREEN + " successfully " + ConsoleColors.GRAY + "completed.");
        }
    }

}
