package de.polocloud.signs;

import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.signs.config.SignConfig;
import de.polocloud.signs.converter.SignConverter;
import de.polocloud.signs.executes.ExecuteService;
import de.polocloud.signs.scheduler.SignProtectionRunnable;
import de.polocloud.signs.signs.cache.IGameServerSignCache;
import de.polocloud.signs.signs.initializer.IGameServerSignInitializer;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class SignService {

    private static SignService instance;
    private final IConfigLoader configLoader;
    private final IConfigSaver configSaver;
    private final ExecuteService executeService;
    private final SignProtectionRunnable signProtectionRunnable;
    private SignConfig signConfig;
    private IGameServerSignCache cache;

    public SignService() throws ExecutionException, InterruptedException {

        instance = this;

        this.configLoader = new SimpleConfigLoader();
        this.configSaver = new SimpleConfigSaver();

        File configPath = new File("plugins/sign/");
        if (!configPath.exists()) configPath.mkdirs();

        this.signConfig = loadConfig(new File("plugins/sign/config.json"));

        this.cache = new IGameServerSignCache();

        this.executeService = new ExecuteService();

        new SignConverter();
        new IGameServerSignInitializer();

        this.signProtectionRunnable = new SignProtectionRunnable();
    }

    public static SignService getInstance() {
        return instance;
    }

    private SignConfig loadConfig(File file) {
        SignConfig masterConfig = configLoader.load(SignConfig.class, file);
        configSaver.save(masterConfig, file);
        return masterConfig;
    }

    public SignConfig getSignConfig() {
        return signConfig;
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

    public ExecuteService getExecuteService() {
        return executeService;
    }

    public SignProtectionRunnable getSignProtectionRunnable() {
        return signProtectionRunnable;
    }
}
