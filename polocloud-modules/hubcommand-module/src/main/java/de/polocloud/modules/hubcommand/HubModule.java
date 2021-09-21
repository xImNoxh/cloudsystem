package de.polocloud.modules.hubcommand;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.module.CloudModule;
import de.polocloud.modules.hubcommand.pluginside.commands.HubCommand;

import java.io.File;

public class HubModule {

    /**
     * Instance of the HubCommand Module
     */
    private static HubModule instance;

    /**
     * Instance of CloudModule
     */
    private final CloudModule module;

    public HubModule(CloudModule module) {
        instance = this;
        this.module = module;

        if (PoloCloudAPI.getInstance().getType().isPlugin()) {
            PoloCloudAPI.getInstance().getCommandManager().registerCommand(new HubCommand());
        }

    }


    public static HubModule getInstance() {
        return instance;
    }

}
