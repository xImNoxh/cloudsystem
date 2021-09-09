package de.polocloud.npcs.bootstraps;

import de.polocloud.api.module.CloudModule;
import de.polocloud.api.module.ModuleCopyType;
import de.polocloud.api.module.info.ModuleInfo;
import de.polocloud.api.module.info.ModuleState;
import de.polocloud.api.module.info.ModuleTask;
import de.polocloud.npcs.module.ModuleNPCService;

@ModuleInfo(main = ModuleBootstrap.class, name = "NPCsModule", version = "1.0", authors = {"PoloCloud", "iPommes"}, copyTypes = {ModuleCopyType.LOBBIES})
public class ModuleBootstrap extends CloudModule {

    private ModuleBootstrap instance;
    private ModuleNPCService npcService;

    @ModuleTask(id = 1, state = ModuleState.LOADING)
    public void loadModule() {
        instance = this;
        npcService = new ModuleNPCService(this);
    }

    @ModuleTask(id = 3, state = ModuleState.RELOADING)
    public void reloadModule() {
        this.npcService.reloadNPCs();
    }

    public ModuleBootstrap getInstance() {
        return instance;
    }

    public ModuleNPCService getNpcService() {
        return npcService;
    }
}
