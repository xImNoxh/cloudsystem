package de.polocloud.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;

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

    public void reportException(Throwable throwable, String type, String cloudVersion) {
        System.out.println(PoloCloudClient.PREFIX + "Sending exception to PoloCloud Servers...");
        new Thread(() -> {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                String content = "Java-version: " + getJavaVersion() + "\n type: " + type + "\n cloudversion: " + cloudVersion + "\n exception: " + sw;
                URL url = new URL("http://" + PoloCloudClient.getInstance().getUrl() + ":" + PoloCloudClient.getInstance().getPort() + "/api/v2/exception/");
                URLConnection con = url.openConnection();
                HttpURLConnection http = (HttpURLConnection) con;
                http.setRequestMethod("POST");
                http.setDoOutput(true);

                http.setFixedLengthStreamingMode(content.length());
                http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                http.connect();

                try (OutputStream os = http.getOutputStream()) {
                    os.write(content.getBytes(StandardCharsets.UTF_8));
                }
                System.out.println(PoloCloudClient.PREFIX + "Successfully reported exception to PoloCloud Servers!");
                sw.close();
                pw.close();
            } catch (MalformedURLException e) {
                System.err.println(PoloCloudClient.PREFIX + "Failed to report exception, host wasn't found!");
            } catch (ProtocolException e) {
                System.err.println(PoloCloudClient.PREFIX + "Failed to report exception, error during handling protocol");
            } catch (IOException exception) {
                System.err.println(PoloCloudClient.PREFIX + "Failed to report exception, IOException occurred");
            }

        }).start();
    }
}
