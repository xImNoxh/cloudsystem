package de.polocloud.launcher;

import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.console.ConsoleColors;
import de.polocloud.client.PoloCloudClient;
import de.polocloud.client.PoloCloudUpdater;
import de.polocloud.launcher.config.LauncherConfig;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import static de.polocloud.api.config.FileConstants.BOOTSTRAP_FILE;
import static de.polocloud.api.config.FileConstants.OLD_BOOTSTRAP_FILE;

public class Launcher {

    public static String PREFIX = "PoloCloud » ";

    private static boolean devMode = false;

    private static LauncherConfig launcherConfig;

    public static void main(String[] args) throws NoSuchMethodException {

        //Initializing the PoloCloudClient for the PoloCloudUpdater and for the ExceptionReporterService
        PoloCloudClient client = new PoloCloudClient("37.114.60.129", 4542);

        //Checking the args for the type of bootstrap and for the devMode
        if (args.length == 1) {
            if (!(args[0].equalsIgnoreCase("Master") || args[0].equalsIgnoreCase("Wrapper"))) {
                System.out.println(PREFIX + args[0] + " is wrong argument!");
                return;
            }
        } else if (args.length >= 2) {
            if (!(args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("true"))) {
                System.out.println("Please specify in a boolean ('true', 'false') if you want to enable the devmode!");
                return;
            }
            devMode = Boolean.parseBoolean(args[1]);
        } else {
            System.out.println(PREFIX + "Please specify what you want to start. (Master, Wrapper)");
            return;
        }

        //Loading the configs
        IConfigLoader loader = new SimpleConfigLoader();
        IConfigSaver saver = new SimpleConfigSaver();

        boolean firstCreation = !new File("launcher.json").exists();
        launcherConfig = loader.load(LauncherConfig.class, new File("launcher.json"));

        if (firstCreation && (args.length == 3 && args[2].equalsIgnoreCase("--ignoreUpdater"))) {
            launcherConfig.setUseUpdater(false);
        }

        saver.save(launcherConfig, new File("launcher.json"));

        //Checking the current bootstrap.jar File
        boolean forceUpdate = launcherConfig.isForceUpdate();
        File bootstrapFile = BOOTSTRAP_FILE;
        if (!bootstrapFile.exists()) {
            forceUpdate = true;
        }


        //Checking for Updates
        if (launcherConfig.isUseUpdater() || !bootstrapFile.exists()) {
            checkForUpdates(launcherConfig.getVersion(), forceUpdate);
        }

        //Checking the bootstrap.jar File, if not exists the cloud can't start -> shutdown
        if (!bootstrapFile.exists()) {
            System.out.println(PoloCloudClient.PREFIX + ConsoleColors.RED + "Couldn't launch Cloud, the download seems to has failed and the boostrap.jar wasn't downloaded! Cloud will shutdown.");
            System.exit(1);
        }

        //Launching the bootstrap.jar File
        try {
            //First attempt
            launchBootstrap0(args);
        } catch (ZipException exception) {
            //Failed to launch the jar, maybe the jar is corrupt after bad update
            //Launcher is trying to rescue the cloud with the old-bootstrap.jar backup file

            System.out.println(PoloCloudClient.PREFIX + ConsoleColors.RED + "Jar couldn't be launched, checking backup Jar...");
            File backup = OLD_BOOTSTRAP_FILE;

            //Applying backup file
            if (backup.exists()) {
                backup.renameTo(FileConstants.BOOTSTRAP_FILE);
                System.out.println(PoloCloudClient.PREFIX + ConsoleColors.RED + "Backup File existed and recovered, try to launch...");
                try {
                    //Second launch try
                    launchBootstrap0(args);
                } catch (ZipException exception1) {
                    System.out.println(PoloCloudClient.PREFIX + ConsoleColors.RED + "Second attempt failed! Stopping");
                    System.out.println(PoloCloudClient.PREFIX + ConsoleColors.RED + "It seems you bootstrap.jar file is corrupt. Please download the cloud new. If the problem is existing in the future too, contact our support!");
                    System.exit(-1);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    client.getExceptionReportService().reportException(e, "launcher - booting bootstrap (" + args[0] + ")", launcherConfig.getVersion(), client.getClientConfig().getReportType());
                }
            } else {
                System.out.println(PoloCloudClient.PREFIX + ConsoleColors.RED + "No Backup version was found! Also it seems you bootstrap.jar file is corrupt. Please download the cloud new. If the problem is existing in the future too, contact our support!");
                System.exit(-1);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            client.getExceptionReportService().reportException(e, "launcher - booting bootstrap (" + args[0] + ")", launcherConfig.getVersion(), client.getClientConfig().getReportType());
        }
    }

    private static void launchBootstrap0(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException {
        File bootstrapFile = BOOTSTRAP_FILE;

        String mainClass;
        JarFile jarFile = new JarFile(bootstrapFile);
        mainClass = jarFile.getManifest().getMainAttributes().getValue("Main-Class");
        if (mainClass == null) {
            System.err.println("Failed to find Main-Class!");
            System.exit(-1);
        }

        //JarFile is not corrupt, launcher can clean up backup Jars
        if (launcherConfig.isUseUpdater() && OLD_BOOTSTRAP_FILE.exists()) {
            System.out.println(PoloCloudClient.PREFIX + "Cleaning up...");
            OLD_BOOTSTRAP_FILE.delete();
            System.out.println(PoloCloudClient.PREFIX + "Done!");
        }

        //Creating classloader from the file, and search the main-class
        ClassLoader classLoader = new URLClassLoader(new URL[]{bootstrapFile.toURI().toURL()});
        Method method = classLoader.loadClass(mainClass).getMethod("main", String[].class);

        //Starting the Main-Thread
        Thread thread = new Thread(() -> {
            try {
                method.invoke(null, (Object) args);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        }, "PoloCloud-Main-Thread");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setContextClassLoader(classLoader);
        thread.start();
    }

    private static void checkForUpdates(String currentVersion, boolean forceUpdate) {
        File bootstrap = BOOTSTRAP_FILE;
        PoloCloudUpdater updater = new PoloCloudUpdater(devMode, currentVersion, "bootstrap", bootstrap);

        if (forceUpdate) {
            if (devMode) {
                System.out.println(PoloCloudClient.PREFIX + "[Updater] Downloading latest development builds...");
                bootstrap.renameTo(OLD_BOOTSTRAP_FILE);
                if (updater.download()) {
                    System.out.println(PoloCloudClient.PREFIX + "[Updater] Successfully downloaded newest development build!");
                } else {
                    System.out.println(PoloCloudClient.PREFIX + "[Updater] Couldn't download latest development build!");
                }
            } else {
                System.out.println(PoloCloudClient.PREFIX + "[Updater] Force update activated. Downloading latest build...");
                bootstrap.renameTo(OLD_BOOTSTRAP_FILE);
                if (updater.download()) {
                    launcherConfig.setVersion(updater.getFetchedVersion());
                    IConfigSaver saver = new SimpleConfigSaver();
                    saver.save(launcherConfig, new File("launcher.json"));
                    System.out.println(PoloCloudClient.PREFIX + "[Updater] Successfully downloaded newest build!");
                } else {
                    System.out.println(PoloCloudClient.PREFIX + "[Updater] Couldn't download latest build!");
                }
            }
        } else if (devMode) {
            System.out.println(PoloCloudClient.PREFIX + "[Updater] Downloading latest development builds...");
            bootstrap.renameTo(OLD_BOOTSTRAP_FILE);
            if (updater.download()) {
                System.out.println(PoloCloudClient.PREFIX + "[Updater] Successfully downloaded newest development build!");
            } else {
                System.out.println(PoloCloudClient.PREFIX + "[Updater] Couldn't download latest development build!");
            }
        } else {
            System.out.println(PoloCloudClient.PREFIX + "[Updater] Searching for updates...");
            if (updater.check()) {
                System.out.println(PoloCloudClient.PREFIX + "[Updater] Found a update! (" + currentVersion + " -> " + updater.getFetchedVersion() + " (Upload date: " + updater.getLastUpdate() + "))");
                System.out.println(PoloCloudClient.PREFIX + "[Updater] downloading...");
                bootstrap.renameTo(OLD_BOOTSTRAP_FILE);
                if (updater.download()) {
                    launcherConfig.setVersion(updater.getFetchedVersion());
                    IConfigSaver saver = new SimpleConfigSaver();

                    saver.save(launcherConfig, new File("launcher.json"));
                    System.out.println(PoloCloudClient.PREFIX + "[Updater] Successfully downloaded latest version! (" + updater.getFetchedVersion() + ")");
                    System.out.println(PoloCloudClient.PREFIX + "[Updater] Want to see the latest changes? Use the 'changelog' command!");
                } else {
                    System.out.println(PoloCloudClient.PREFIX + "[Updater] Couldn't download latest version!");
                }
            } else {
                System.out.println(PoloCloudClient.PREFIX + "[Updater] You are running the latest version! (" + currentVersion + ")");
            }
        }

    }
}
