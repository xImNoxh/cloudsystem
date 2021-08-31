package de.polocloud.example;

import de.polocloud.api.module.CloudModule;
import de.polocloud.api.module.ModuleCopyType;
import de.polocloud.api.module.info.ModuleInfo;
import de.polocloud.api.module.info.ModuleState;
import de.polocloud.api.module.info.ModuleTask;
import de.polocloud.example.config.ExampleConfig;

import java.io.File;
import java.util.UUID;

@ModuleInfo(
    main = ExampleModule.class,
    name = "ExampleModule",
    version = "1.0",
    authors = "Lystx",
    copyTypes = ModuleCopyType.NONE
)
public class ExampleModule extends CloudModule {

    private ExampleConfig config;

    private static ExampleModule instance;

    public ExampleModule() {
        instance = this;
    }

    @ModuleTask(id = 1, state = ModuleState.LOADING)
    public void loadModule() {
        System.out.println("Loading Example module...");
        this.config = configLoader.load(ExampleConfig.class, new ExampleConfig(true, "yourS", UUID.randomUUID()), new File(this.dataDirectory, "config.json"), this.configSaver);

    }

    @ModuleTask(id = 2, state = ModuleState.STARTING)
    public void enableModule() {
    }

    @ModuleTask(id = 3, state = ModuleState.RELOADING)
    public void reloadModule() {
    }

    @ModuleTask(id = 4, state = ModuleState.STOPPING)
    public void stopModule() {
    }

    public ExampleConfig getConfig() {
        return config;
    }

    public static ExampleModule getInstance() {
        return instance;
    }
}
