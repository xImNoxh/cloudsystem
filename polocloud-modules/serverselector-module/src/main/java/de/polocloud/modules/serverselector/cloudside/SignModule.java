package de.polocloud.modules.serverselector.cloudside;

import de.polocloud.api.module.CloudModule;
import de.polocloud.api.module.ModuleCopyType;
import de.polocloud.api.module.info.ModuleInfo;
import de.polocloud.api.module.info.ModuleState;
import de.polocloud.api.module.info.ModuleTask;
import de.polocloud.modules.serverselector.api.SignAPI;
import de.polocloud.modules.serverselector.cloudside.api.ModuleCloudAPI;
import de.polocloud.modules.serverselector.cloudside.api.ModuleGlobalAPI;

import java.io.File;

@ModuleInfo(
    main = SignModule.class,
    name = "PoloCloud-Signs",
    version = "1.0",
    authors = "Lystx",
    copyTypes = {ModuleCopyType.LOBBIES}
)
public class SignModule extends CloudModule {

    @ModuleTask(id = 1, state = ModuleState.LOADING)
    public void loadModule() {

        new SignAPI(
            null, //Module is not bukkit instance
            new ModuleCloudAPI(),
            new ModuleGlobalAPI(
                new File(getDataDirectory(), "config.json"),
                new File(getDataDirectory(), "signs.json")
            )
        );

        SignAPI.getInstance().getGlobalAPI().load();
    }

    @ModuleTask(id = 3, state = ModuleState.RELOADING)
    public void reloadModule() {
        SignAPI.getInstance().reload();
    }

    @ModuleTask(id = 4, state = ModuleState.STOPPING)
    public void shutdown() {
        SignAPI.getInstance().shutdown();
    }

}

