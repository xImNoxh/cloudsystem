package de.polocloud.wrapper.version;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.console.ConsoleColors;
import de.polocloud.api.gameserver.helper.GameServerVersion;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.wrapper.Wrapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class was developed by DasLixou :3
 */

public class VersionInstaller {

    private static CopyOnWriteArrayList<Process> runningPatchers = new CopyOnWriteArrayList<>();

    public static boolean installVersion(GameServerVersion version) {
        PoloLogger.print(LogLevel.INFO, "§7Downloading §b" + version.getTemplateType().getDisplayName() + "-Version§7... (§3" + version.getTitle() + "§7)");

        //Downloading the Version normal if it isn't patchable
        if(!version.isPatchable()){
            File downloadedVersion = new File(FileConstants.WRAPPER_STORAGE_VERSIONS, version.getTitle() + ".jar");
            downloadedVersion.getParentFile().mkdirs();
            try {
                FileUtils.copyURLToFile(new URL(version.getUrl()), downloadedVersion);
            } catch (IOException e) {
                e.printStackTrace();
                PoloLogger.print(LogLevel.ERROR, "§cFailed to download §b" + version.getTemplateType().getDisplayName() + "-Version§7... (§3" + version.getTemplateType() + "§7)");
                return false;
            }

            PoloLogger.print(LogLevel.INFO, "Downloading of (§3" + version.getTitle() + "§7)" + ConsoleColors.GREEN + " successfully " + ConsoleColors.GRAY + "completed.");
            return true;
        }


        //Downloading and patching the Serverversion
        //Creating the Folder for running the patcher in it
        File patchServer = new File(FileConstants.WRAPPER_PATCHER_FOLDER, version.getTitle());
        File patchedFile = new File(patchServer, version.getTitle() + ".jar");

        patchServer.mkdirs();
        try {
            FileUtils.copyURLToFile(new URL(version.getUrl()), patchedFile);
        } catch (IOException e) {
            e.printStackTrace();
            PoloLogger.print(LogLevel.ERROR, "§cFailed to download §b" + version.getTemplateType().getDisplayName() + "-Version§7... (§3" + version.getTemplateType() + "§7)");
            return false;
        }

        PoloLogger.print(LogLevel.INFO, "Downloading of (§3" + version.getTitle() + "§7)" + ConsoleColors.GREEN + " successfully " + ConsoleColors.GRAY + "completed.");

        PoloLogger.print(LogLevel.INFO, "§7Trying to patch §b" + version.getTemplateType().getDisplayName() + "-Version§7... (§3" + version.getTitle() + "§7)");

        if(!Wrapper.getInstance().hasEnoughMemory(1536)) {
            PoloLogger.print(LogLevel.WARNING, "§cCouldn't patch §b" + version.getTemplateType().getDisplayName() + "-Version§7... (§3" + version.getTemplateType() + "§7), §cbecause not enough memory is available");
            return false;
        }
            String[] command = new String[]{
                "java",
                "-XX:+UseG1GC",
                "-XX:MaxGCPauseMillis=50",
                "-XX:-UseAdaptiveSizePolicy",
                "-XX:CompileThreshold=100",
                "-Dpaperclip.patchonly=true",
                "-Dio.netty.recycler.maxCapacity=0",
                "-Dio.netty.recycler.maxCapacity.default=0",
                "-Djline.terminal=jline.UnsupportedTerminal",
                "-Xmx" + 1536 + "M",
                "-jar",
                version.getTitle() + ".jar",
                "nogui"
            };

        try{
            Process patcherProcess = new ProcessBuilder(command)
                .directory(patchServer)
                .start();
            runningPatchers.add(patcherProcess);

            Scheduler.runtimeScheduler().schedule(() ->{
                if(patcherProcess.isAlive()){
                    PoloLogger.print(LogLevel.WARNING, "§7Patcher of version §b" + version.getTitle() + " §7took to long (50secs), terminating process...");
                    patcherProcess.destroy();
                    runningPatchers.remove(patcherProcess);
                }
            }, 50000L);

            int exitCode = patcherProcess.waitFor();
            runningPatchers.remove(patcherProcess);
            if(exitCode == 0){
                for (File file : Objects.requireNonNull(new File(patchServer, "cache").listFiles())) {
                    if(file.getName().contains("patched")){
                        patchedFile = file;
                    }
                }
            }else{
                PoloLogger.print(LogLevel.ERROR, "§cPatcher of version §b" + version.getTitle() + " §creturned exitcode §b" + exitCode + " §c, this code wasn't expected!");
                return false;
            }
        }catch (IOException | InterruptedException exception){
            exception.printStackTrace();
            PoloCloudAPI.getInstance().reportException(exception);
        }

        PoloLogger.print(LogLevel.INFO, "Patch of (§3" + version.getTitle() + "§7)" + ConsoleColors.GREEN + " successfully " + ConsoleColors.GRAY + "completed.");

        File serverFile = new File(FileConstants.WRAPPER_STORAGE_VERSIONS, version.getTitle() + ".jar");
        serverFile.getParentFile().mkdirs();

        try {
            FileUtils.copyFile(patchedFile, serverFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PoloHelper.deleteFolder(patchServer);
        return true;
    }

    public static void shutdownPatchers(){
        if(!runningPatchers.isEmpty()){
            PoloLogger.print(LogLevel.INFO, "§7Trying to stop §b" + runningPatchers.size() + " §7Version-Patchers...");
            for (Process runningPatcher : runningPatchers) {
                if(runningPatcher.isAlive()){
                    runningPatcher.destroy();
                }
            }
        }
    }

}
