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

    private static HubCommandModule instance;
    private CloudModule module;

    private HubCommandMessageChannel hubCommandMessageChannel;
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

    public void reload(){
        loadConfig();
        forceUpdate();
    }

    public void forceUpdate(){
        hubCommandMessageChannel.getMessageChannel().sendMessage(new WrappedObject<>(hubCommandConfig));
    }

    public void loadConfig(){
        hubCommandConfig = new SimpleConfigLoader().load(HubCommandConfig.class, new File(module.getDataDirectory(), "config.yml"));
    }

    public void saveConfig(){
        new SimpleConfigSaver().save(hubCommandConfig, new File(module.getDataDirectory(), "config.yml"));
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
