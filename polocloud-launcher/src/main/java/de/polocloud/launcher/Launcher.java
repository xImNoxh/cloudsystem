package de.polocloud.launcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.polocloud.client.PoloCloudClient;
import de.polocloud.client.PoloCloudUpdater;
import de.polocloud.updater.UpdateClient;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

public class Launcher {

    public static String PREFIX = "PoloCloud » ";

    private static boolean devMode = false;

    private static File configFile;
    private static Map<String, Object> configObject;

    public static void main(String[] args) {

        //TODO change to MainServer

        //Inits the PoloCloudClient for the PoloCloudUpdater and for the ExceptionReporterService
        PoloCloudClient client = new PoloCloudClient("127.0.0.1", 4542);

        //Checking the args for the type of bootstrap and for the devMode
        if(args.length == 1){
            if (!(args[0].equalsIgnoreCase("Master") || args[0].equalsIgnoreCase("Wrapper"))) {
                System.out.println(PREFIX + args[0] + " is wrong argument!");
                return;
            }
        }else if(args.length == 2){
            if(!(args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("true"))){
                System.out.println("Please specify in a boolean ('true', 'false') if you want to enable the devmode!");
                return;
            }
            devMode = Boolean.parseBoolean(args[1]);
        }else{
            System.out.println(PREFIX + "Please specify what you want to start. (Master, Wrapper)");
            return;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //Loading the config
        configFile = new File("launcher.json");
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
        File bootstrapFile = new File("bootstrap.jar");
        if (!bootstrapFile.exists()) {
            forceUpdate = true;
        }


        //Checking for Updates
        checkForUpdates(currentVersion, forceUpdate, gson);

        /**
         * Old Method for native Support if the new UpdateServer isn't started or not functional
         */
        //checkForUpdatesNative(currentVersion, forceUpdate, gson);


        //Checking the bootstrap.jar File, if not exists the cloud can't start -> shutdown
        if (!bootstrapFile.exists()) {
            System.out.println("Couldn't launch Cloud, the download seems to has failed and the boostrap.jar wasn't downloaded! Cloud will shutdown.");
            System.exit(1);
        }

        //Launching the bootstrap.jar File
        try (JarFile jarFile = new JarFile(bootstrapFile)) {

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

        } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            client.getExceptionReportService().reportException(e, "launcher - booting bootstrap (" + args[0] + ")", currentVersion);
        }
    }


    private static void checkForUpdates(String currentVersion, boolean forceUpdate, Gson gson){
        PoloCloudUpdater updater = new PoloCloudUpdater(devMode, currentVersion, "bootstrap", new File("bootstrap.jar"));

        if(forceUpdate){
            if(devMode){
                System.out.println(PREFIX + "[Updater] Downloading latest development builds...");
                if (updater.download()) {
                    System.out.println(PREFIX + "[Updater] Successfully downloaded newest development build!");
                } else {
                    System.out.println(PREFIX + "[Updater] Couldn't download latest development build!");
                }
            }else{
                System.out.println(PREFIX + "[Updater] Force update activated. Downloading latest build...");
                if (updater.download()) {
                    System.out.println(PREFIX + "[Updater] Successfully downloaded newest build!");
                } else {
                    System.out.println(PREFIX + "[Updater] Couldn't download latest build!");
                }
            }
        }else if(devMode){
            System.out.println(PREFIX + "[Updater] Downloading latest development builds...");
            if (updater.download()) {
                System.out.println(PREFIX + "[Updater] Successfully downloaded newest development build!");
            } else {
                System.out.println(PREFIX + "[Updater] Couldn't download latest development build!");
            }
        }else{
            System.out.println(PREFIX + "[Updater] Searching for updates...");
            if (updater.check()) {
                System.out.println(PREFIX + "[Updater] Found a update! (" + currentVersion + " -> " + updater.getFetchedVersion() + " (Upload date: " + updater.getLastUpdate() + "))");
                System.out.println(PREFIX + "[Updater] downloading...");
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

    private static void checkForUpdatesNative(String currentVersion, boolean forceUpdate, Gson gson){

        String baseUrl = "http://37.114.60.129:8870";
        String downloadUrl = baseUrl + "/updater/download/bootstrap";
        String versionUrl = baseUrl + "/updater/version/bootstrap";
        UpdateClient updateClient = new UpdateClient(downloadUrl, new File("bootstrap.jar"), versionUrl, currentVersion);

        System.out.println(PREFIX + "#Checking for bootstrap updates... (" + updateClient.getClientVersion() + ")");
        if (updateClient.download(forceUpdate)) {
            configObject.remove("version");
            configObject.put("version", updateClient.getFetchedVersion());

            try {
                FileWriter writer = new FileWriter(configFile);
                gson.toJson(configObject, writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(PREFIX + "#updated");
        } else {
            System.out.println(PREFIX + "#no update found!");
        }
    }

}
