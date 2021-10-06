package de.polocloud.wrapper.version;

import de.polocloud.api.config.FileConstants;
import de.polocloud.api.console.ConsoleColors;
import de.polocloud.api.gameserver.helper.GameServerVersion;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.util.PoloHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;

/**
 * This class was developed by DasLixou :3
 * */

public class VersionInstaller {

    public static void installVersion(GameServerVersion version) {
        PoloLogger.print(LogLevel.INFO, "§7Downloading §b" + version.getTemplateType().getDisplayName() + "-Version§7... (§3" + version.getTitle() + "§7)");

        File patchServer = new File(FileConstants.WRAPPER_PATCHER_FOLDER, version.getTitle());
        File patchedFile = new File(patchServer, version.getTitle() + ".jar");

        patchServer.mkdirs();
        try {
            FileUtils.copyURLToFile(new URL(version.getUrl()), patchedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PoloLogger.print(LogLevel.INFO, "Downloading of (§3" + version.getTitle() + "§7)" + ConsoleColors.GREEN + " successfully " + ConsoleColors.GRAY + "completed.");
        if(version.isPatchable()) {
            PoloLogger.print(LogLevel.INFO, "§7Patching §b" + version.getTemplateType().getDisplayName() + "-Version§7... (§3" + version.getTitle() + "§7)");


            String[] command = new String[]{
                "java",
                "-XX:+UseG1GC",
                "-XX:MaxGCPauseMillis=50",
                "-XX:-UseAdaptiveSizePolicy",
                "-XX:CompileThreshold=100",
                "-Dcom.mojang.eula.agree=true",
                "-Dio.netty.recycler.maxCapacity=0",
                "-Dio.netty.recycler.maxCapacity.default=0",
                "-Djline.terminal=jline.UnsupportedTerminal",
                "-Xmx" + 1536 + "M",
                "-jar",
                version.getTitle() + ".jar",
                "nogui"
            };

            try {
                ProcessBuilder processBuilder = new ProcessBuilder(command).directory(patchServer);
                Process process = null;
                process = processBuilder.start();

                Scanner reader = new Scanner(process.getInputStream(), "UTF-8");
                Scanner errors = new Scanner(process.getErrorStream(), "UTF-8");
                boolean nextLineStop = false;
                boolean read = true;
                while (read) {
                    if(reader.hasNext()) {
                        String line = reader.nextLine();
                        if (line == null) {
                            continue;
                        }
                        System.out.println(line);
                        if(nextLineStop) {
                            read = false;
                        }
                        if(line.startsWith("Patching")) {
                            nextLineStop = true;
                        }
                    }
                    if(errors.hasNext()) {
                        System.err.println(errors.nextLine());
                    }

                }
                process.destroy();
                patchedFile = Objects.requireNonNull(new File(patchServer, "cache").listFiles((FilenameFilter) (dir, name) -> {
                    if (name.startsWith("patched_")) {
                        return true;
                    }
                    return false;
                }))[0];
            } catch (IOException e) {
                e.printStackTrace();
            }

            PoloLogger.print(LogLevel.INFO, "Patch of (§3" + version.getTitle() + "§7)" + ConsoleColors.GREEN + " successfully " + ConsoleColors.GRAY + "completed.");
        }

        File serverFile = new File(FileConstants.WRAPPER_STORAGE_VERSIONS, version.getTitle() + ".jar");
        serverFile.getParentFile().mkdirs();

        try {
            FileUtils.copyFile(patchedFile, serverFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PoloHelper.deleteFolder(patchServer);
    }

}
