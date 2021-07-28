package de.polocloud.hub;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.module.Module;

public class CloudModule extends Module {


    @Override
    public void onLoad() {
        CloudAPI.getInstance().getCommandPool().registerCommand(CloudAPI.getInstance().getGuice().getInstance(HubCloudCommand.class));

    }

    @Override
    public void onShutdown() {

    }
}
