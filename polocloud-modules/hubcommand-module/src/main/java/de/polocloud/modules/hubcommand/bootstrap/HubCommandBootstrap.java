package de.polocloud.modules.hubcommand.bootstrap;

import de.polocloud.api.module.CloudModule;
import de.polocloud.api.module.ModuleCopyType;
import de.polocloud.api.module.info.ModuleInfo;
import de.polocloud.api.module.info.ModuleState;
import de.polocloud.api.module.info.ModuleTask;
import de.polocloud.modules.hubcommand.HubCommandModule;

@ModuleInfo(
    main = HubCommandBootstrap.class,
    name = "HubCommand",
    version = "1.0",
    description = "This is a HubCommand Module",
    authors = "HttpMarco",
    copyTypes = ModuleCopyType.PROXIES
)
public class HubCommandBootstrap extends CloudModule {

    private HubCommandModule proxyModule;

    @ModuleTask(id = 1, state = ModuleState.LOADING)
    public void load() {
        proxyModule = new HubCommandModule(this);
    }

    @ModuleTask(id = 3, state = ModuleState.RELOADING)
    public void reload() {
        proxyModule.reload();
    }

}
