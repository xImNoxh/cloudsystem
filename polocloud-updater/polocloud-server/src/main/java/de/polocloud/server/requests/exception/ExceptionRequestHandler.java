package de.polocloud.server.requests.exception;

import de.polocloud.server.PoloCloudServer;
import io.javalin.http.util.RateLimit;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ExceptionRequestHandler {

    private final SimpleDateFormat simpleDateFormat;

    public ExceptionRequestHandler() {
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        PoloCloudServer.getInstance().getJavalin().post("/api/v2/exception/", context -> {
            new RateLimit(context).requestPerTimeUnit(35, TimeUnit.MINUTES);
            String postData = context.req.getReader().lines().collect(Collectors.joining());
            String ipAddress = context.req.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = context.req.getRemoteAddr();
            }
            PoloCloudServer.getInstance().getThreadProvider().getExceptionWriter().saveException(ipAddress, simpleDateFormat.format(System.currentTimeMillis()), postData);
        });
    }

}
