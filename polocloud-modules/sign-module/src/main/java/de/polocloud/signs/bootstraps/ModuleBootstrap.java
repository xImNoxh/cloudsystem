package de.polocloud.signs.bootstraps;

import de.polocloud.api.module.CloudModule;
import de.polocloud.api.module.ModuleCopyType;
import de.polocloud.api.module.info.ModuleInfo;
import de.polocloud.api.module.info.ModuleState;
import de.polocloud.api.module.info.ModuleTask;
import de.polocloud.signs.module.ModuleSignService;

@ModuleInfo(main = ModuleBootstrap.class, name = "SignModule", version = "1.0", authors = {"PoloCloud", "iPommes"}, copyTypes = {ModuleCopyType.LOBBIES})
public class ModuleBootstrap extends CloudModule {

    private static ModuleBootstrap instance;
    private ModuleSignService signService;

    @ModuleTask(id = 1, state = ModuleState.LOADING)
    public void loadModule() {
        instance = this;
        signService = new ModuleSignService(this);
    }

    @ModuleTask(id = 3, state = ModuleState.RELOADING)
    public void reloadModule() {
        this.signService.reloadSigns();
    }

    public static ModuleBootstrap getInstance() {
        return instance;
    }

    public ModuleSignService getSignService() {
        return signService;
    }
}

