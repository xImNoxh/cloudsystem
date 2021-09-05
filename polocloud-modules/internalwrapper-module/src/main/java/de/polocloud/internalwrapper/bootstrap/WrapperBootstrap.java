package de.polocloud.internalwrapper.bootstrap;

import de.polocloud.api.module.CloudModule;
import de.polocloud.api.module.ModuleCopyType;
import de.polocloud.api.module.info.ModuleInfo;
import de.polocloud.api.module.info.ModuleState;
import de.polocloud.api.module.info.ModuleTask;
import de.polocloud.api.module.info.ScheduledModuleTask;
import de.polocloud.internalwrapper.InternalWrapper;

@ModuleInfo(
    main = WrapperBootstrap.class,
    name = "InternalWrapper",
    version = "1.0",
    description = "This replaces an extra Wrapper-Instance",
    authors = "Lystx",
    copyTypes = ModuleCopyType.NONE,
    reloadable = true
)
public class WrapperBootstrap extends CloudModule {

    private final InternalWrapper wrapper;

    public WrapperBootstrap() {
        this.wrapper = new InternalWrapper(this);
    }

    @ModuleTask(id = 1, state = ModuleState.LOADING)
    public void load() {
        wrapper.load();
    }

    @ModuleTask(id = 2, state = ModuleState.STARTING)
    @ScheduledModuleTask(delay = 20L)
    public void enable() {
        wrapper.connect();
    }

    @ModuleTask(id = 4, state = ModuleState.STOPPING)
    public void stop() {
        wrapper.shutdown();
    }
}
