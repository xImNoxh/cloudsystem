package de.polocloud.signs;

import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.signs.config.SignConfig;
import de.polocloud.signs.signs.cache.IGameServerSignCache;

import java.io.File;

public class SignService {

    private static SignService instance;
    private SignConfig signConfig;

    private IGameServerSignCache cache;

    private final IConfigLoader configLoader;
    private final IConfigSaver configSaver;


    public SignService() {

        instance = this;
        this.signConfig = loadConfig(new File("config.json"));

        this.cache = new IGameServerSignCache();

        this.configLoader = new SimpleConfigLoader();
        this.configSaver = new SimpleConfigSaver();
    }

    private SignConfig loadConfig(File file) {
        SignConfig masterConfig = configLoader.load(SignConfig.class, file);
        configSaver.save(masterConfig, file);
        return masterConfig;
    }


    public SignConfig getSignConfig() {
        return signConfig;
    }

    public static SignService getInstance() {
        return instance;
    }

    public IConfigLoader getConfigLoader() {
        return configLoader;
    }

    public IConfigSaver getConfigSaver() {
        return configSaver;
    }

    public IGameServerSignCache getCache() {
        return cache;
    }
}
