package de.polocloud.signs;

import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.signs.commands.CloudSignsCommand;
import de.polocloud.signs.config.SignConfig;
import de.polocloud.signs.signs.IGameServerSign;
import de.polocloud.signs.signs.cache.IGameServerSignCache;
import de.polocloud.signs.signs.initializer.IGameServerSignInitializer;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class SignService {

    private static SignService instance;
    private SignConfig signConfig;

    private IGameServerSignCache cache;

    private final IConfigLoader configLoader;
    private final IConfigSaver configSaver;


    public SignService() throws ExecutionException, InterruptedException {

        instance = this;

        this.configLoader = new SimpleConfigLoader();
        this.configSaver = new SimpleConfigSaver();
        this.signConfig = loadConfig(new File("config.json"));

        this.cache = new IGameServerSignCache();

        new IGameServerSignInitializer();
    }

    private SignConfig loadConfig(File file) {
        SignConfig masterConfig = configLoader.load(SignConfig.class, file);
        configSaver.save(masterConfig, file);
        return masterConfig;
    }

    public IGameServerSign getFreeTemplateSign(IGameServer gameServer){
        return cache.stream().filter(key -> key.getGameServer() == null &&
            key.getTemplate().getName().equals(gameServer.getTemplate().getName())).findAny().orElse(null);
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
