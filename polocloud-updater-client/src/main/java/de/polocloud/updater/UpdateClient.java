package de.polocloud.updater;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UpdateClient {

    private static final Gson gson = new GsonBuilder().create();

    private String downloadUrl;
    private File destinationFile;
    private String versionUrl;

    private String clientVersion;

    public UpdateClient(String downloadUrl, File destinationFile, String versionUrl, String clientVersion) {
        this.downloadUrl = downloadUrl;
        this.destinationFile = destinationFile;
        this.versionUrl = versionUrl;
        this.clientVersion = clientVersion;
    }


    public boolean download(boolean force) {

        if (check() || force) {

            try {
                FileUtils.copyURLToFile(new URL(downloadUrl), destinationFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;

        }
        return false;

    }

    private boolean check() {
        InputStream in = null;
        boolean result = false;
        try {
            in = new URL(versionUrl).openStream();

            String fetchedVersionJsonString = IOUtils.toString(in, StandardCharsets.UTF_8);

            JsonObject jsonObject = gson.fromJson(fetchedVersionJsonString, JsonObject.class);
            String fetchedVersion = jsonObject.get("currentVersion").getAsString();

            System.out.println("fetched version: " + fetchedVersion);
            System.out.println("clientVersion: " + this.clientVersion);

            result = !fetchedVersion.equalsIgnoreCase(this.clientVersion);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public File getDestinationFile() {
        return destinationFile;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getVersionUrl() {
        return versionUrl;
    }
}
