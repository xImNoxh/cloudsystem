package de.polocloud.launcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.polocloud.api.config.FileConstants;
import de.polocloud.client.PoloCloudClient;
import de.polocloud.client.PoloCloudUpdater;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import static de.polocloud.api.config.FileConstants.*;

public class Launcher {

    public static String PREFIX = "PoloCloud » ";

    private static boolean devMode = false;

    private static File configFile;
    private static Map<String, Object> configObject;

    public static void main(String[] args) {

        //Initializing the PoloCloudClient for the PoloCloudUpdater and for the ExceptionReporterService
        PoloCloudClient client = new PoloCloudClient("37.114.60.98", 4542);

        //Checking the args for the type of bootstrap and for the devMode
        if (args.length == 1) {
            if (!(args[0].equalsIgnoreCase("Master") || args[0].equalsIgnoreCase("Wrapper"))) {
                System.out.println(PREFIX + args[0] + " is wrong argument!");
                return;
            }
        } else if (args.length == 2) {
            if (!(args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("true"))) {
                System.out.println("Please specify in a boolean ('true', 'false') if you want to enable the devmode!");
                return;
            }
            devMode = Boolean.parseBoolean(args[1]);
        } else {
            System.out.println(PREFIX + "Please specify what you want to start. (Master, Wrapper)");
            return;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //Loading the config
        configFile = LAUNCHER_FILE;
        configObject = null;
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();

                configObject = new HashMap<>();
                configObject.put("version", "0.Alpha");
                configObject.put("forceUpdate", true);

                FileWriter writer = new FileWriter(configFile);
                gson.toJson(configObject, writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Getting the current Information from the config
        String currentVersion = "-1";
        boolean forceUpdate = true;
        try {
            FileReader reader = new FileReader(configFile);
            configObject = gson.fromJson(reader, HashMap.class);
            currentVersion = (String) configObject.get("version");
            forceUpdate = (boolean) configObject.get("forceUpdate");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        //Checking the current bootstrap.jar File
        File bootstrapFile = BOOTSTRAP_FILE;
        if (!bootstrapFile.exists()) {
            forceUpdate = true;
        }


        //Checking for Updates
        checkForUpdates(currentVersion, forceUpdate, gson);

        /*
          Old Method for native Support if the new UpdateServer isn't started or not functional
         */
        //checkForUpdatesNative(currentVersion, forceUpdate, gson);


        //Checking the bootstrap.jar File, if not exists the cloud can't start -> shutdown
        if (!bootstrapFile.exists()) {
            System.out.println("Couldn't launch Cloud, the download seems to has failed and the boostrap.jar wasn't downloaded! Cloud will shutdown.");
            System.exit(1);
        }

        //Launching the bootstrap.jar File
        try {
            //First attempt
            launchBootstrap(args);
        } catch (ZipException exception) {
            //Failed to launch the jar, maybe the jar is corrupt after bad update
            //Launcher is trying to rescue the cloud with the old-bootstrap.jar backup file

            System.out.println("Jar couldn't be launched, going back to backup version");
            File backup = OLD_BOOTSTRAP_FILE;

            //Applying backup file
            if (backup.exists()) {
                backup.renameTo(FileConstants.BOOTSTRAP_FILE);
                System.out.println("Backup File existed, try to launch...");
                try {
                    //Second launch try
                    launchBootstrap(args);
                } catch (ZipException exception1) {
                    System.out.println("Second attempt failed! Stopping");
                    System.exit(-1);
                } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                    client.getExceptionReportService().reportException(e, "launcher - booting bootstrap (" + args[0] + ")", currentVersion);
                }
            } else {
                System.out.println("No backup version was found. Please contact the support or try later!");
            }
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            client.getExceptionReportService().reportException(e, "launcher - booting bootstrap (" + args[0] + ")", currentVersion);
        }

        //Launching the bootstrap.jar File
//        try (JarFile jarFile = new JarFile(bootstrapFile)) {
//
//            URLClassLoader classLoader = new URLClassLoader(new URL[]{bootstrapFile.toURI().toURL()});
//            URLClassLoader urlClassLoader = new URLClassLoader(((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs());
//            Thread.currentThread().setContextClassLoader(classLoader);
//
//
//            String mainClass = "de.polocloud.bootstrap.Bootstrap";
//            Class<?> loadedClass = classLoader.loadClass(mainClass);
//            Method mainMethod = loadedClass.getMethod("main", String[].class);
//
//            final Object[] params = new Object[1];
//            params[0] = args;
//
//            urlClassLoader.close();
//            mainMethod.invoke(null, params);
//
//            System.out.println(PREFIX + "[Updater] Cleaning up...");
//            OLD_BOOTSTRAP_FILE.delete();
//            System.out.println(PREFIX + "[Updater] Done!");
//        } catch (ZipException exception){
//            System.out.println("Jar couldn't be launched, going back to backup version");
//            File backup = OLD_BOOTSTRAP_FILE;
//            if(backup.exists()){
//                backup.renameTo(BOOTSTRAP_FILE);
//                System.out.println("Backup File existed, try to launch...");
//            }else{
//                System.out.println("No backup version was found. Please contact the support!");
//            }
//        } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
//            e.printStackTrace();
//            client.getExceptionReportService().reportException(e, "launcher - booting bootstrap (" + args[0] + ")", currentVersion);
//        }
    }

    private static void launchBootstrap(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        File bootstrapFile = BOOTSTRAP_FILE;

        //Creating JarFile
        new JarFile(bootstrapFile);

        //JarFile is not corrupt, launcher can clean up backup Jars
        System.out.println(PREFIX + "[Updater] Cleaning up...");
        OLD_BOOTSTRAP_FILE.delete();
        System.out.println(PREFIX + "[Updater] Done!");

        //Loading mainclass
        URLClassLoader classLoader = new URLClassLoader(new URL[]{bootstrapFile.toURI().toURL()});
        URLClassLoader urlClassLoader = new URLClassLoader(((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs());
        Thread.currentThread().setContextClassLoader(classLoader);

        String mainClass = "de.polocloud.bootstrap.Bootstrap";
        Class<?> loadedClass = classLoader.loadClass(mainClass);
        Method mainMethod = loadedClass.getMethod("main", String[].class);

        final Object[] params = new Object[1];
        params[0] = args;

        urlClassLoader.close();
        mainMethod.invoke(null, params);
    }

    private static void checkForUpdates(String currentVersion, boolean forceUpdate, Gson gson) {
        File bootstrap = BOOTSTRAP_FILE;
        PoloCloudUpdater updater = new PoloCloudUpdater(devMode, currentVersion, "bootstrap", bootstrap);

        if (forceUpdate) {
            if (devMode) {
                System.out.println(PREFIX + "[Updater] Downloading latest development builds...");
                bootstrap.renameTo(OLD_BOOTSTRAP_FILE);
                if (updater.download()) {
                    System.out.println(PREFIX + "[Updater] Successfully downloaded newest development build!");
                } else {
                    System.out.println(PREFIX + "[Updater] Couldn't download latest development build!");
                }
            } else {
                System.out.println(PREFIX + "[Updater] Force update activated. Downloading latest build...");
                bootstrap.renameTo(OLD_BOOTSTRAP_FILE);
                if (updater.download()) {
                    configObject.remove("version");
                    configObject.put("version", updater.getFetchedVersion());
                    try {
                        FileWriter writer = new FileWriter(configFile);
                        gson.toJson(configObject, writer);
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        PoloCloudClient.getInstance().getExceptionReportService().reportException(e, "launcher", currentVersion);
                    }
                    System.out.println(PREFIX + "[Updater] Successfully downloaded newest build!");
                } else {
                    System.out.println(PREFIX + "[Updater] Couldn't download latest build!");
                }
            }
        } else if (devMode) {
            System.out.println(PREFIX + "[Updater] Downloading latest development builds...");
            bootstrap.renameTo(OLD_BOOTSTRAP_FILE);
            if (updater.download()) {
                System.out.println(PREFIX + "[Updater] Successfully downloaded newest development build!");
            } else {
                System.out.println(PREFIX + "[Updater] Couldn't download latest development build!");
            }
        } else {
            System.out.println(PREFIX + "[Updater] Searching for updates...");
            if (updater.check()) {
                System.out.println(PREFIX + "[Updater] Found a update! (" + currentVersion + " -> " + updater.getFetchedVersion() + " (Upload date: " + updater.getLastUpdate() + "))");
                System.out.println(PREFIX + "[Updater] downloading...");
                bootstrap.renameTo(OLD_BOOTSTRAP_FILE);
                if (updater.download()) {
                    configObject.remove("version");
                    configObject.put("version", updater.getFetchedVersion());
                    try {
                        FileWriter writer = new FileWriter(configFile);
                        gson.toJson(configObject, writer);
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        PoloCloudClient.getInstance().getExceptionReportService().reportException(e, "launcher", currentVersion);
                    }
                    System.out.println(PREFIX + "[Updater] Successfully downloaded latest version! (" + updater.getFetchedVersion() + ")");
                    System.out.println(PREFIX + "[Updater] Want to see the latest changes? Use the 'changelog' command!");
                } else {
                    System.out.println(PREFIX + "[Updater] Couldn't download latest version!");
                }
            } else {
                System.out.println(PREFIX + "[Updater] You are running the latest version! (" + currentVersion + ")");
            }
        }

    }
}
