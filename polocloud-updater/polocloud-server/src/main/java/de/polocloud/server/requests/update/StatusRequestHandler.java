package de.polocloud.server.requests.update;


import de.polocloud.server.PoloCloudServer;
import de.polocloud.server.statistics.PoloCloudServerStatisticsService;
import io.javalin.http.util.RateLimit;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class StatusRequestHandler {

    private final SimpleDateFormat simpleDateFormat;

    public StatusRequestHandler() {
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        PoloCloudServer.getInstance().getJavalin().get("/api/v2/update/status", context -> {
            new RateLimit(context).requestPerTimeUnit(15, TimeUnit.MINUTES);
            File boostrap = new File("release/" + PoloCloudServer.getInstance().getConfig().getBoostrapFileName());
            File api = new File("release/" + PoloCloudServer.getInstance().getConfig().getApiFileName());
            File devBoostrap = new File("dev/" + PoloCloudServer.getInstance().getConfig().getBoostrapFileName());
            File devAPI = new File("dev/" + PoloCloudServer.getInstance().getConfig().getApiFileName());

            String response = "{\n" +
                    "    \"status\": {\n" +
                    "        \"apiavailable\": " + api.exists() + ",\n" +
                    "        \"bootstrapavailable\": " + boostrap.exists() + ",\n" +
                    "        \"devapiavailable\": " + devAPI.exists() + ",\n" +
                    "        \"devbootstrapavailable\": " + devBoostrap.exists() + "\n" +
                    "    },\n" +
                    "    \"api\": {\n" +
                    "        \"version\": \"" + (api.exists() ? PoloCloudServer.getInstance().getConfig().getApiVersion() : "not available") + "\",\n" +
                    "        \"filename\": \"" + (api.exists() ? PoloCloudServer.getInstance().getConfig().getApiFileName() : "not available") + "\",\n" +
                    "        \"lastModified\": \"" + (api.exists() ? simpleDateFormat.format(api.lastModified()) : "not available") + "\"\n" +
                    "    },\n" +
                    "    \"bootstrap\": {\n" +
                    "        \"version\": \"" + (boostrap.exists() ? PoloCloudServer.getInstance().getConfig().getBoostrapVersion() : "not available") + "\",\n" +
                    "        \"filename\": \"" + (boostrap.exists() ? boostrap.getName() : "not available") + "\",\n" +
                    "        \"lastModified\": \"" + (boostrap.exists() ? simpleDateFormat.format(boostrap.lastModified()) : "not available") + "\"\n" +
                    "    }\n" +
                    "}";
            context.result(response);
            PoloCloudServerStatisticsService.addRequestedStatusAmount();
            PoloCloudServerStatisticsService.saveStatisticsConfig();
        });
    }
}
