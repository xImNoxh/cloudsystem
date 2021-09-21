package de.polocloud.modules.hubcommand.cloudside;

import de.polocloud.api.module.CloudModule;
import de.polocloud.api.module.ModuleCopyType;
import de.polocloud.api.module.info.ModuleInfo;
import de.polocloud.api.module.info.ModuleState;
import de.polocloud.api.module.info.ModuleTask;
import de.polocloud.modules.hubcommand.HubModule;


@ModuleInfo(
    main = ModuleBootstrap.class,
    name = "PoloCloud-HubCommand",
    version = "1.0",
    description = "Module for sending players back to a fallback",
    authors = "HttpMarco",
    copyTypes = ModuleCopyType.PROXIES
)
public class ModuleBootstrap extends CloudModule {

    private HubModule proxyModule;

    @ModuleTask(id = 1, state = ModuleState.LOADING)
    public void load() {
        proxyModule = new HubModule(this);
    }

}
