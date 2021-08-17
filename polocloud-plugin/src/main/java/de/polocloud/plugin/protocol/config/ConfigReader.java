package de.polocloud.plugin.protocol.config;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.api.server.SimpleGameServer;
import de.polocloud.plugin.protocol.NetworkClient;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

public class ConfigReader {

    public static String getMasterAddress() {
        String masterAddress = "127.0.0.1";
        try {
            File parentFile = new File(NetworkClient.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile();
            parentFile = new File(parentFile + "/PoloCloud.json");

            Gson gson = new GsonBuilder().create();
            FileReader reader = new FileReader(parentFile);
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            masterAddress = jsonObject.get("Master-Address").getAsString();

            String serverName = jsonObject.get("GameServer-Name").getAsString();
            long serverSnowflake = jsonObject.get("GameServer-Snowflake").getAsLong();

            CloudPlugin.getCloudPluginInstance().setGameServer(new SimpleGameServer(serverName, "", false, GameServerStatus.STARTING, serverSnowflake, -1, System.currentTimeMillis(), -1, -1, -1, null, Lists.newArrayList()));

            reader.close();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return masterAddress;
    }
}
