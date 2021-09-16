package de.polocloud.client;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ChangelogRequestService {

    public String getChangelog(String type, String version) {
        try {
            if (type == null || version == null) {
                throw new RuntimeException("Type or the version was null! Cannot get Changelog");
            }
            String postContent = type + "#" + version;

            URL connectURL = new URL("http://" + PoloCloudClient.getInstance().getUrl() + ":" + PoloCloudClient.getInstance().getPort() + "/api/v2/update/changelog");

            URLConnection connection = connectURL.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setConnectTimeout(2500);

            httpURLConnection.setFixedLengthStreamingMode(postContent.length());
            httpURLConnection.setRequestProperty("Content-Type", "text; charset=UTF-8");
            httpURLConnection.connect();

            try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                outputStream.write(postContent.getBytes(StandardCharsets.UTF_8));
            }

            InputStream stream = httpURLConnection.getInputStream();
            String stringContent = IOUtils.toString(stream, StandardCharsets.UTF_8);
            stream.close();
            return stringContent;
        } catch (MalformedURLException e) {
            System.err.println(PoloCloudClient.PREFIX + "Failed to fetch changelog, host wasn't found!");
        } catch (ProtocolException e) {
            System.err.println(PoloCloudClient.PREFIX + "Failed to fetch changelog, error during handling protocol");
        } catch (IOException exception) {
            System.err.println(PoloCloudClient.PREFIX + "Failed to fetch changelog, IOException occurred");
        }
        return "Cannot fetch changelog!";
    }

}
