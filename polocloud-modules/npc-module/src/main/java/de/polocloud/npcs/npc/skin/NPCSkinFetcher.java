package de.polocloud.npcs.npc.skin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import de.polocloud.api.util.gson.PoloHelper;
import de.polocloud.api.uuid.IUUIDFetcher;
import de.polocloud.api.uuid.SimpleUUIDFetcher;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@Deprecated
public class NPCSkinFetcher {

    /**
     * Cache for the players skin-values
     */
    private final Map<String, String> values;

    /**
     * Cache for the players skin-signatures
     */
    private final Map<String, String> signatures;

    /**
     * Cache for the players uuids
     */
    private final Map<String, String> uuids;

    /**
     * Cache for the players skins
     */
    private final List<String> skins;

    public NPCSkinFetcher() {
        this.values = new HashMap<>();
        this.signatures = new HashMap<>();
        this.uuids = new HashMap<>();
        this.skins = new LinkedList<>();
    }

    /**
     *  Gets the {@link Property} from Mojang
     *  for setting a skin for a NPC
     * @param name of the Player for the Property
     * @return the property of the Player
     */
    public Property getSkinProperty(String name){
        String uuid = fetchUUID(name);
        return new Property("textures", getSkinValue(uuid), getSkinSignature(uuid));
    }

    /**
     *  Fetches the skin-value value from the {@link NPCSkinFetcher#data(String)} method
     * @param uuid for fetching the skin-value from the player
     * @return {@link String} the skin-value of the player's skin
     */
    public String getSkinValue(String uuid) {
        return data(uuid)[0];
    }

    /**
     *  Fetches the skin-signature value from the {@link NPCSkinFetcher#data(String)} method
     * @param uuid for fetching the skin-signature from the player
     * @return {@link String} the skin-signature value of the player's skin
     */
    public String getSkinSignature(String uuid) {
        return data(uuid)[1];
    }

    /**
     *  Fetches the uuid from the player
     *  from the cache if the uuid was fetched already
     *  or from the Mojang servers (https://api.mojang.com)
     * @param name from the player for fetching the uuid
     * @return {@link String} player's uuid
     */
    public String fetchUUID(String name){
        if(this.uuids.containsKey(name)){
            return this.uuids.get(name);
        }else{
            String url = "https://api.mojang.com/users/profiles/minecraft/" + name;

            StringBuilder json = new StringBuilder();
            try {
                Scanner scanner = new Scanner(new URL(url).openStream());
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    while (line.startsWith(" ")) {
                        line = line.substring(1);
                    }
                    json.append(line);
                }
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String uuid = PoloHelper.GSON_INSTANCE.fromJson(json.toString(), JsonObject.class).get("id").getAsString();
            if(uuid != null){
                this.uuids.put(name, uuid);
            }
            return uuid;
        }
    }

    /**
     *  Fetches the skindata from the Mojang-Sessionserver (https://sessionserver.mojang.com)
     * @param uuid for fetching the skin data from the player
     * @return {@link String-array} the data, which contains the signature and the value of the skin
     */
    public String[] data(String uuid) {
        String[] data = new String[2];
        if (!skins.contains(uuid)) {
            skins.add(uuid);
            try {
                URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
                URLConnection uc = url.openConnection();
                uc.setUseCaches(false);
                uc.setDefaultUseCaches(false);
                uc.addRequestProperty("User-Agent", "Mozilla/5.0");
                uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
                uc.addRequestProperty("Pragma", "no-cache");
                String json = new Scanner(uc.getInputStream(), "UTF-8").useDelimiter("\\A").next();
                JsonParser parser = new JsonParser();
                Object obj = parser.parse(json);
                JsonArray properties = (JsonArray) ((JsonObject) obj).get("properties");
                for (Object o : properties) {
                    try {
                        JsonObject property = (JsonObject) o;
                        String value = property.get("value").getAsString();
                        String signature = property.has("signature") ? property.get("signature").getAsString() : null;
                        values.put(uuid, value);
                        signatures.put(uuid, signature);
                        data[0] = value;
                        data[1] = signature;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            data[0] = values.get(uuid);
            data[1] = signatures.get(uuid);
        }
        return data;
    }

}
