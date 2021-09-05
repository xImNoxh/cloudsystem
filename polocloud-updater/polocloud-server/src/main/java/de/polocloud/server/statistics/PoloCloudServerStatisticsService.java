package de.polocloud.server.statistics;

import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.server.PoloCloudServer;

import java.io.File;

public class PoloCloudServerStatisticsService {

    private static IConfigSaver configSaver = new SimpleConfigSaver();


    public static void addRequestedStatusAmount() {
        PoloCloudServer.getInstance().getStatisticsConfig().setRequestedStatusAmount(PoloCloudServer.getInstance().getStatisticsConfig().getRequestedStatusAmount() + 1);
    }

    public static void addRequestedAPIDownloadAmount() {
        PoloCloudServer.getInstance().getStatisticsConfig().setRequestedAPIDownloads(PoloCloudServer.getInstance().getStatisticsConfig().getRequestedAPIDownloads() + 1);
    }

    public static void addRequestedBoostrapDownloadAmount() {
        PoloCloudServer.getInstance().getStatisticsConfig().setRequestedBoostrapDownloads(PoloCloudServer.getInstance().getStatisticsConfig().getRequestedAPIDownloads() + 1);
    }

    public static void saveStatisticsConfig() {
        configSaver.save(PoloCloudServer.getInstance().getStatisticsConfig(), new File("statistics.json"));
    }

}
