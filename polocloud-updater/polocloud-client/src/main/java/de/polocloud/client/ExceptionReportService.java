package de.polocloud.client;

import com.google.common.base.Throwables;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.client.enumeration.ReportType;
import de.polocloud.client.threaded.exception.ExceptionWriter;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

public class ExceptionReportService {

    public static int getJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2);
        }
        int dotPos = version.indexOf('.');
        int dashPos = version.indexOf('-');
        return Integer.parseInt(version.substring(0,
                dotPos > -1 ? dotPos : dashPos > -1 ? dashPos : 1));
    }

    public void reportException(Throwable throwable, String type, String cloudVersion, ReportType reportType) {
        new Thread(() -> {
            try {
                //Generating the Exception StackTrace
                String stacktrace = Throwables.getStackTraceAsString(throwable);

                //Generating the POST content for the different ReportTypes
                String content;
                if(reportType.equals(ReportType.MINIMAL)){
                    content = "Cloud-Type: " + type + "\n Cloud-Version: " + cloudVersion + "\n StackTrace: " + stacktrace;
                    ExceptionWriter.saveException(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()), content);
                }else if(reportType.equals(ReportType.OPTIONAL)){
                    content = "Java-Version: " + getJavaVersion() + "\n Type: " + type + "\n Cloud-Version: " + cloudVersion + "\n Excpetion: " + stacktrace;
                    ExceptionWriter.saveException(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()), content);
                }else{
                    ExceptionWriter.saveException(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()), "StackTrace: " + stacktrace);
                    return;
                }

                //Connecting to the PoloCloud Servers
                URL url = new URL("http://" + PoloCloudClient.getInstance().getUrl() + ":" + PoloCloudClient.getInstance().getPort() + "/api/v2/exception/");
                URLConnection con = url.openConnection();
                HttpURLConnection http = (HttpURLConnection) con;
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                http.setConnectTimeout(2500);

                http.setFixedLengthStreamingMode(content.length());
                http.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
                http.connect();

                try (OutputStream os = http.getOutputStream()) {
                    os.write(content.getBytes(StandardCharsets.UTF_8));
                }
                PoloLogger.print(LogLevel.INFO,PoloCloudClient.PREFIX + "A new §bexception §cwas reported to the PoloCloud Servers. §7If you don't want that, or §cdeactivate §7this, use '§bcloudreports disable§7'");
                PoloLogger.print(LogLevel.INFO, PoloCloudClient.PREFIX + "This exception was also reported locally in the data/reports folder.");
                PoloCloudClient.getInstance().getClientConfig().setReportedExceptions(PoloCloudClient.getInstance().getClientConfig().getReportedExceptions()+1);
                new SimpleConfigSaver().save(PoloCloudClient.getInstance().getClientConfig(), new File("client.json"));
            } catch (IOException ignored) {
            }
        }).start();
    }
}
