package de.polocloud.server.requests.update;

import de.polocloud.server.PoloCloudServer;
import io.javalin.http.util.RateLimit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ChangelogRequestHandler {

    public ChangelogRequestHandler() {

        PoloCloudServer.getInstance().getJavalin().post("/api/v2/update/changelog", context ->{
            //Implement Javalin's RateLimiter
            new RateLimit(context).requestPerTimeUnit(15, TimeUnit.MINUTES);

            String rawPostData = context.req.getReader().lines().collect(Collectors.joining());
            String[] changelogDataArray = rawPostData.split("#");

            if(changelogDataArray.length != 2){
                context.result("400#bad request, wrong args");
                return;
            }
            String type = changelogDataArray[0];
            String version = changelogDataArray[1];
            if(!type.equalsIgnoreCase("api") && !type.equalsIgnoreCase("bootstrap")) {
                context.result("400#bad request, invalid type");
                return;
            }
            File requestedChangelogFile = new File("changelog/" + type.toLowerCase() + "/" + version.toLowerCase() + ".txt");
            if(!requestedChangelogFile.exists()){
                context.result("404#file not found");
                return;
            }
            try (BufferedReader br = new BufferedReader(new FileReader(requestedChangelogFile))) {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                String everything = sb.toString();
                context.result(everything);
            }
        });

    }
}
