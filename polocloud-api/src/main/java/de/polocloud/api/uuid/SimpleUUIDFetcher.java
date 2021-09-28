package de.polocloud.api.uuid;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.util.gson.PoloHelper;
import de.polocloud.api.util.map.UniqueMap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleUUIDFetcher implements IUUIDFetcher {

    /**
     * The executor service for performance
     */
    private final ExecutorService executor;

    /**
     * The cache name-uuid-cache
     */
    private final UniqueMap<String, UUID> cache;

    /**
     * Sets the Thread amount for the Provider
     *
     * @param threads the amount of threads
     */
    public SimpleUUIDFetcher(int threads) {
        this(Executors.newFixedThreadPool(threads));
    }

    /**
     * Loads the UUIDProvider with a given
     *
     * @param executor ExecutorService
     */
    public SimpleUUIDFetcher(ExecutorService executor) {
        this.executor = executor;
        this.cache = new UniqueMap<>();
    }

    @Override
    public UUID getUniqueId(String playerName)  {
        if (this.cache.get().containsKey(playerName)) {
            return this.cache.get().atKey(playerName);
        }
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() == 400) {
                System.err.println("There is no player with the name \"" + playerName + "\"!");
                return UUID.randomUUID();
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            JsonData object = new JsonData(stringBuilder.toString());

            String uuidAsString = object.getString("id");

            this.cache.put(object.getString("name")).toValue(parseUUIDFromString(uuidAsString));
            return parseUUIDFromString(uuidAsString);
        } catch (Exception e) {
            PoloCloudAPI.getInstance().reportException(e);
            PoloLogger.print(LogLevel.ERROR, "An exception was caught while fetching a uuid for a player (" + playerName + ")!");
            return null;
        }
    }

    @Override
    public String getName(UUID uuid) {
        if (this.cache.get().containsValue(uuid)) {
            return this.cache.get().atValue(uuid);
        }
     
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format("https://api.mojang.com/user/profiles/%s/names", uuid.toString().replace("-", ""))).openConnection();
            connection.setReadTimeout(5000);
            connection.connect();

            //NEU
            JsonElement parse = PoloHelper.GSON_INSTANCE.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), JsonElement.class);

            if (parse.toString().equals("{}")) {
                return null;
            }

            JsonArray jsonArray = (JsonArray) parse;

            JsonData jsonObject = new JsonData(jsonArray.get(jsonArray.size() - 1).toString());
            String name = jsonObject.getString("name");
            UUID id = UUID.fromString(jsonObject.getString("id"));

            cache.put(name).toValue(id);
            return name;
        } catch (Exception ex) {
            PoloCloudAPI.getInstance().reportException(ex);
            PoloLogger.print(LogLevel.ERROR, "An exception was caught while fetching a name for a player (" + uuid + ")!");
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    @Override
    public UUID parseUUIDFromString(String uuidAsString) {
        String[] parts = {
                "0x" + uuidAsString.substring(0, 8),
                "0x" + uuidAsString.substring(8, 12),
                "0x" + uuidAsString.substring(12, 16),
                "0x" + uuidAsString.substring(16, 20),
                "0x" + uuidAsString.substring(20, 32)
        };

        long mostSigBits = Long.decode(parts[0]);
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(parts[1]);
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(parts[2]);

        long leastSigBits = Long.decode(parts[3]);
        leastSigBits <<= 48;
        leastSigBits |= Long.decode(parts[4]);

        return new UUID(mostSigBits, leastSigBits);
    }

}
