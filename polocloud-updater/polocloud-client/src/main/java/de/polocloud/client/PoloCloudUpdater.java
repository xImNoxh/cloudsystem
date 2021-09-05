package de.polocloud.client;

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

public class PoloCloudUpdater {

    private static final Gson gson = new GsonBuilder().create();

    private final boolean devMode;
    private final String currentVersion;
    private final String type;
    private final File target;
    private String fetchedVersion;
    private String lastUpdate;

    public PoloCloudUpdater(boolean devMode, String currentVersion, String type, File target) {
        this.devMode = devMode;
        this.currentVersion = currentVersion;
        this.type = type;
        this.target = target;
    }

    public boolean isAvailable() {
        try (InputStream inputStream = new URL("http://" + PoloCloudClient.getInstance().getUrl() + ":" + PoloCloudClient.getInstance().getPort() + "/api/v2/update/status").openConnection().getInputStream()) {

            String stringContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            if (stringContent == null) {
                return false;
            }
            JsonObject mainObject = gson.fromJson(stringContent, JsonObject.class);
            JsonObject typeObject = mainObject.getAsJsonObject(type);
            if (typeObject == null) {
                return false;
            }
            this.fetchedVersion = typeObject.get("version").getAsString();
            this.lastUpdate = typeObject.get("lastModified").getAsString();
            return isAvailable(mainObject.getAsJsonObject("status"));
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean isAvailable(JsonObject statusObject) {
        if (statusObject == null) {
            return false;
        }
        return Boolean.parseBoolean(statusObject.get((devMode ? "dev" : "") + type + "available").getAsString());
    }

    public boolean check() {
        InputStream inputStream = null;
        try {
            URL url = new URL("http://" + PoloCloudClient.getInstance().getUrl() + ":" + PoloCloudClient.getInstance().getPort() + "/api/v2/update/status");
            inputStream = url.openConnection().getInputStream();
            String stringContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            if (stringContent == null) {
                return false;
            }

            JsonObject mainObject = gson.fromJson(stringContent, JsonObject.class);
            boolean available = isAvailable(mainObject.getAsJsonObject("status"));
            if (devMode) {
                return available;
            }
            if (available) {
                JsonObject typeObject = mainObject.getAsJsonObject(type);
                if (typeObject == null) {
                    return false;
                }
                this.fetchedVersion = typeObject.get("version").getAsString();
                this.lastUpdate = typeObject.get("lastModified").getAsString();
                return !this.fetchedVersion.equalsIgnoreCase(this.currentVersion);
            }

        } catch (IOException ignored) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
        return false;
    }

    public boolean download() {
        if (isAvailable()) {
            try {
                if (!target.exists()) {
                    target.createNewFile();
                }
                FileUtils.copyURLToFile(new URL("http://" + PoloCloudClient.getInstance().getUrl() + ":" + PoloCloudClient.getInstance().getPort() + "/api/v2/download/" + (devMode ? "dev/" : "") + type), target);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public void autoDownload() {
        if (check() || devMode) {
            download();
        }
    }

    public String getFetchedVersion() {
        return fetchedVersion;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }
}
