package de.polocloud.modules.serverselector.api;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.modules.serverselector.api.config.SignConfiguration;
import de.polocloud.modules.serverselector.api.elements.SignData;
import de.polocloud.modules.serverselector.api.subapi.BukkitSignAPI;
import de.polocloud.modules.serverselector.api.subapi.CloudSignAPI;
import de.polocloud.modules.serverselector.api.subapi.GlobalSignAPI;
import lombok.Getter;

@Getter
public class SignAPI {

    /**
     * The global instance for this {@link SignAPI}
     */
    @Getter
    private static SignAPI instance;

    /**
     * The bukkit side api to handle from bukkit
     */
    private final BukkitSignAPI bukkitSideAPI;

    /**
     * The cloud side api to handle from module
     */
    private final CloudSignAPI cloudSideAPI;

    /**
     * The global api to handle from bukkit
     * and cloud side
     */
    private final GlobalSignAPI globalAPI;

    /**
     * The channel to pass around the config
     */
    private final IMessageChannel<SignConfiguration> configChannel;

    /**
     * The channel to pass around the current sign data
     */
    private final IMessageChannel<SignData> dataChannel;

    public SignAPI(BukkitSignAPI bukkitSideAPI, CloudSignAPI cloudSideAPI, GlobalSignAPI globalAPI) {
        instance = this;
        this.globalAPI = globalAPI;
        this.bukkitSideAPI = bukkitSideAPI;
        this.cloudSideAPI = cloudSideAPI;

        this.configChannel = PoloCloudAPI.getInstance().getMessageManager().registerChannel(SignConfiguration.class, "sign-module-config-channel");
        this.dataChannel = PoloCloudAPI.getInstance().getMessageManager().registerChannel(SignData.class, "sign-module-data-channel");
    }


    /**
     * Reloads this api
     */
    public void reload() {
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
            this.configChannel.sendMessage(SignAPI.getInstance().getGlobalAPI().getConfig());
            this.dataChannel.sendMessage(new SignData(this.globalAPI.getCurrentCloudSigns()));
        } else {
            //TODO: PLUGIN SIDE
        }
    }

    /**
     * Stops this api
     */
    public void shutdown() {
        this.globalAPI.save();
    }
}
