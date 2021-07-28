package de.polocloud.hub;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.module.Module;
import de.polocloud.hub.config.HubConfig;

import java.io.File;

public class CloudModule extends Module {

    private static CloudModule instance;
    private HubConfig hubConfig;

    @Override
    public void onLoad() {
        instance = this;
        hubConfig = loadHubConfig();

        if(hubConfig.getUse()) {
            CloudAPI.getInstance().getCommandPool().registerCommand(CloudAPI.getInstance().getGuice().getInstance(HubCloudCommand.class));
        }
    }

    @Override
    public void onShutdown() {

    }

    public HubConfig loadHubConfig() {
        File configPath = new File("modules/hub/");
        if(!configPath.exists()) configPath.mkdirs();
        File configFile = new File("modules/hub/config.json");
        HubConfig hubConfig = getConfigLoader().load(HubConfig.class, configFile);
        getConfigSaver().save(hubConfig, configFile);
        return hubConfig;
    }

    public static CloudModule getInstance() {
        return instance;
    }

    public HubConfig getHubConfig() {
        return hubConfig;
    }
}
