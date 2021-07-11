package de.polocloud.bootstrap;

import de.polocloud.logger.log.Logger;

public class Bootstrap {

    public static String currentVersion = "0.1.Alpha";

    public static void main(String[] args) {
        /*
        try {
            File destinationFile = new File(Bootstrap.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI());

            String baseUrl = "http://127.0.0.1:8870";
            String downloadUrl = baseUrl + "/updater/download/bootstrap";
            String versionUrl = baseUrl + "/updater/version/";
            UpdateClient updateClient = new UpdateClient(downloadUrl, destinationFile, versionUrl, currentVersion);
            System.out.println("checking for updates...");
            if (updateClient.download(false)) {
                //restart
                System.out.println("updated");
                System.out.println("Please restart your Cloud!");
            }else{
                System.out.println("no update found!");
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

         */
        Logger.boot();
        new CloudInit(args);
    }

}
