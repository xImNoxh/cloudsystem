package de.polocloud.server.requests.update;


import de.polocloud.server.PoloCloudServer;
import de.polocloud.server.statistics.PoloCloudServerStatisticsService;
import io.javalin.http.util.RateLimit;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;

public class UpdateRequestHandler {

    public void registerDownloads() {
        PoloCloudServer.getInstance().getJavalin().get("/api/v2/download/bootstrap", context -> {
            new RateLimit(context).requestPerTimeUnit(10, TimeUnit.MINUTES);
            File boostrap = new File("release/" + PoloCloudServer.getInstance().getConfig().getBoostrapFileName());
            if (boostrap.exists()) {
                context.result(new FileInputStream(boostrap));
            } else {
                context.result("404#not found");
            }
            PoloCloudServerStatisticsService.addRequestedBoostrapDownloadAmount();
            PoloCloudServerStatisticsService.saveStatisticsConfig();
        });
        PoloCloudServer.getInstance().getJavalin().get("/api/v2/download/api", context -> {
            new RateLimit(context).requestPerTimeUnit(10, TimeUnit.MINUTES);
            File api = new File("release/" + PoloCloudServer.getInstance().getConfig().getApiFileName());
            if (api.exists()) {
                context.result(new FileInputStream(api));
            } else {
                context.result("404#not found");
            }
            PoloCloudServerStatisticsService.addRequestedAPIDownloadAmount();
            PoloCloudServerStatisticsService.saveStatisticsConfig();
        });
        PoloCloudServer.getInstance().getJavalin().get("/api/v2/download/dev/api", context -> {
            new RateLimit(context).requestPerTimeUnit(10, TimeUnit.MINUTES);
            File api = new File("dev/" + PoloCloudServer.getInstance().getConfig().getApiFileName());
            if (api.exists()) {
                context.result(new FileInputStream(api));
            } else {
                context.result("404#not found");
            }
            PoloCloudServerStatisticsService.addRequestedAPIDownloadAmount();
            PoloCloudServerStatisticsService.saveStatisticsConfig();
        });
        PoloCloudServer.getInstance().getJavalin().get("/api/v2/download/dev/bootstrap", context -> {
            new RateLimit(context).requestPerTimeUnit(10, TimeUnit.MINUTES);
            File boostrap = new File("dev/" + PoloCloudServer.getInstance().getConfig().getBoostrapFileName());
            if (boostrap.exists()) {
                context.result(new FileInputStream(boostrap));
            } else {
                context.result("404#not found");
            }
            PoloCloudServerStatisticsService.addRequestedBoostrapDownloadAmount();
            PoloCloudServerStatisticsService.saveStatisticsConfig();
        });
    }

}
