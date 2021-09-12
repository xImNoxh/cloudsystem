package de.polocloud.modules.hubcommand;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.module.CloudModule;
import de.polocloud.api.util.WrappedObject;
import de.polocloud.modules.hubcommand.channel.HubCommandMessageChannel;
import de.polocloud.modules.hubcommand.config.HubCommandConfig;
import de.polocloud.modules.hubcommand.events.CloudCollectiveEvents;

import java.io.File;

public class HubCommandModule {

    /**
     * Instance of the HubCommand Module
     */
    private static HubCommandModule instance;

    /**
     * Instance of CloudModule
     */
    private final CloudModule module;

    /**
     * The {@link de.polocloud.api.messaging.IMessageChannel} instance
     */
    private final HubCommandMessageChannel hubCommandMessageChannel;

    /**
     * The MainConfig
     */
    private HubCommandConfig hubCommandConfig;

    public HubCommandModule(CloudModule module) {
        instance = this;
        this.module = module;

        this.hubCommandMessageChannel = new HubCommandMessageChannel();

        loadConfig();
        saveConfig();

        PoloCloudAPI.getInstance().getEventManager().registerListener(new CloudCollectiveEvents());
        forceUpdate();
    }

    /**
     * Reloads the config and sends the Config to every proxy
     */
    public void reload(){
        loadConfig();
        forceUpdate();
    }

    /**
     * Sends the {@link HubCommandConfig} over the {@link de.polocloud.api.messaging.IMessageChannel} to everyProxy
     */
    public void forceUpdate(){
        hubCommandMessageChannel.getMessageChannel().sendMessage(hubCommandConfig);
    }

    /**
     * Loads all values from of the {@link HubCommandConfig}
     */
    public void loadConfig(){
        hubCommandConfig = new SimpleConfigLoader().load(HubCommandConfig.class, new File(module.getDataDirectory(), "config.json"));
    }

    /**
     * Saves the current state of the {@link HubCommandConfig}
     */
    public void saveConfig(){
        new SimpleConfigSaver().save(hubCommandConfig, new File(module.getDataDirectory(), "config.json"));
    }

    public HubCommandMessageChannel getHubCommandMessageChannel() {
        return hubCommandMessageChannel;
    }

    public HubCommandConfig getHubCommandConfig() {
        return hubCommandConfig;
    }

    public static HubCommandModule getInstance() {
        return instance;
    }

}
