package de.polocloud.modules.serverselector.api.elements;

import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a Group of CloudSigns
 * with Ids
 *
 * Could look like:
 * Lobby:
 *    1 : Lobby-1
 *    2 : Lobby-2
 *    3 : Lobby-3
 * BedWars:
 *    1 : BedWars-1
 *    2 : BedWars-2
 *    3 : BedWars-3
 *
 * It's simple to understand the logic of this
 * To sort the Signs in the SignSelector in Bukkit
 * the Signs in the {@link SignGroup} are already
 * declared with an ID to iterate through all the signs
 * easily
 */
@Data
public class SignGroup {

    /**
     * The name of the sign group
     */
    private final String name;

    /**
     * The cloud signs stored in cache
     */
    private Map<Integer, CloudSign> cloudSigns;

    /**
     * Creates an empty {@link SignGroup} without any
     * {@link CloudSign} stored in it
     *
     * @param name the name of the group
     */
    public SignGroup(String name) {
        this.name = name;
        this.cloudSigns = new HashMap<>();
    }

    /**
     * Creates a new {@link SignGroup} with a name and
     * already provided {@link CloudSign}s
     *
     * @param name the name of the group
     * @param cloudSigns the provided signs
     */
    public SignGroup(String name, List<CloudSign> cloudSigns) {
        this.name = name;
        this.cloudSigns = new HashMap<>();

        HashMap<Integer, CloudSign> map = new HashMap<>();
        int count = 1;
        for (CloudSign cloudSign : cloudSigns) {
            if (cloudSign.getGroup().equalsIgnoreCase(name)) {
                map.put(count, cloudSign);
                count++;
            }
        }
        this.setCloudSigns(map);
    }

}
