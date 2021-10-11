package de.polocloud.modules.serverselector.api.subapi;

import de.polocloud.modules.serverselector.api.config.SignConfiguration;
import de.polocloud.modules.serverselector.api.elements.CloudSign;

import java.util.List;

public interface GlobalSignAPI {

    /**
     * The current {@link SignConfiguration}
     * to get all config values of the module
     */
    SignConfiguration getConfig();

    /**
     * Adds a {@link CloudSign} to this module
     *
     * @param sign the sign
     */
    void addSign(CloudSign sign);

    /**
     * Removes a {@link CloudSign} from this module
     *
     * @param sign the sign
     */
    void removeSign(CloudSign sign);

    /**
     * Gets a {@link CloudSign} at a specific position
     *
     * @param x the x coordinate of the position
     * @param y the x coordinate of the position
     * @param z the z coordinate of the position
     * @param world the world of the position
     * @return sign if found or null otherwise
     */
    CloudSign getCloudSign(int x, int y, int z, String world);

    /**
     * Gets the current cached {@link CloudSign}s
     */
    List<CloudSign> getCurrentCloudSigns();

    /**
     * Loads the config
     */
    void load();

    /**
     * Saves the currently cached values
     */
    void save();
}
