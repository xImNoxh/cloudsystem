package de.polocloud.modules.permission.bootstrap;

import de.polocloud.api.module.CloudModule;
import de.polocloud.api.module.ModuleCopyType;
import de.polocloud.api.module.info.ModuleInfo;
import de.polocloud.api.module.info.ModuleState;
import de.polocloud.api.module.info.ModuleTask;
import de.polocloud.modules.permission.InternalPermissionModule;

@ModuleInfo(
    main = ModuleBootstrap.class,
    name = "PoloCloud-Permissions",
    version = "1.0",
    description = "This manages the perms all over the network",
    authors = "Lystx",
    copyTypes = ModuleCopyType.ALL
)
public class ModuleBootstrap extends CloudModule {

    /**
     * The internal module
     */
    private InternalPermissionModule permissionModule;

    @ModuleTask(id = 1, state = ModuleState.LOADING)
    public void onLoad() {
        this.permissionModule = new InternalPermissionModule(this);
        this.permissionModule.load();
    }

    @ModuleTask(id = 2, state = ModuleState.STARTING)
    public void onStart() {
        this.permissionModule.enable();
    }

    @ModuleTask(id = 3, state = ModuleState.RELOADING)
    public void onReload() {
        this.permissionModule.reload();
    }

    @ModuleTask(id = 4, state = ModuleState.STOPPING)
    public void onShutdown() {
        this.permissionModule.shutdown();
    }
}
