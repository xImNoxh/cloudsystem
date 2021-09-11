package de.polocloud.modules.proxy.cloudside;

import de.polocloud.api.module.CloudModule;
import de.polocloud.api.module.ModuleCopyType;
import de.polocloud.api.module.info.ModuleInfo;
import de.polocloud.api.module.info.ModuleState;
import de.polocloud.api.module.info.ModuleTask;
import de.polocloud.modules.proxy.ProxyModule;

@ModuleInfo(
    main = ModuleBootstrap.class,
    name = "PoloCloud-Proxy",
    version = "1.0",
    description = "This is a Proxy Module",
    authors = "HttpMarco",
    copyTypes = ModuleCopyType.PROXIES
)
public class ModuleBootstrap extends CloudModule {

    private ProxyModule proxyModule;

    @ModuleTask(id = 1, state = ModuleState.LOADING)
    public void load() {
        proxyModule = new ProxyModule(this);
    }

    @ModuleTask(id = 2, state = ModuleState.STARTING)
    public void enable() {
        proxyModule.enable();
    }

    @ModuleTask(id = 3, state = ModuleState.RELOADING)
    public void reload() {
        proxyModule.reload();
    }

    @ModuleTask(id = 4, state = ModuleState.STOPPING)
    public void stop() {
        proxyModule.shutdown();
    }

}
