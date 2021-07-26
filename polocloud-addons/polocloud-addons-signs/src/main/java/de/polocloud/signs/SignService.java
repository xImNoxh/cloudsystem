package de.polocloud.signs;

import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.ITemplate;
import de.polocloud.signs.commands.CloudSignsCommand;
import de.polocloud.signs.executes.SignAddExecute;
import de.polocloud.signs.executes.SignExecute;
import de.polocloud.signs.executes.SignRemoveExecute;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import de.polocloud.signs.cache.SignCache;
import de.polocloud.signs.collectives.CollectiveSignEvents;
import de.polocloud.signs.config.SignConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class SignService {

    private static SignService instance;
    private SignCache cache;

    private SignExecute addSign;
    private SignExecute removeSign;

    private SignConfig signConfig;


    public SignService() {

        instance = this;
        this.cache = new SignCache();

        this.signConfig = loadConfig();

        this.removeSign = new SignRemoveExecute(this);
        this.addSign = new SignAddExecute(this);

        Bukkit.getPluginCommand("cloudsings").setExecutor(new CloudSignsCommand());

        /*
        EventRegistry.registerListener((EventHandler<ChannelActiveEvent>) event -> CloudExecutor.getInstance().getGameServerManager()
            .getGameServerByName("Lobby-1").thenAccept(gameServer -> {
                Location location = new Location(Bukkit.getWorld("world"), -1269, 5, -390);
                for (int i = 0; i < 3; i++) {
                    addSign(gameServer.getTemplate(), location.clone().subtract(i, 0, 0));
                }
                try {
                    new SignAutoLoading(this, gameServer.getTemplate());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }), ChannelActiveEvent.class);

         */
        new CollectiveSignEvents();
    }

    private SignConfig loadConfig() {
        File configFile = new File("config.json");
        IConfigLoader configLoader = new SimpleConfigLoader();
        SignConfig masterConfig = configLoader.load(SignConfig.class, configFile);
        IConfigSaver configSaver = new SimpleConfigSaver();
        configSaver.save(masterConfig, configFile);
        return masterConfig;
    }

    public void addSign(ITemplate template, Location location) {
        cache.add(new CloudSign(template, location));
    }

    public SignConfig getSignConfig() {
        return signConfig;
    }

    public static SignService getInstance() {
        return instance;
    }

    public SignCache getCache() {
        return cache;
    }

    public List<CloudSign> getSignsByTemplate(ITemplate template) {
        return cache.stream().filter(key -> key.getTemplate().getName().equals(template.getName())).collect(Collectors.toList());
    }

    public CloudSign getCloudSignBySign(Sign sign) {
        return cache.stream().filter(key -> key.getSign().equals(sign)).findAny().orElse(null);
    }

    public CloudSign getSignByGameServer(IGameServer gameServer) {
        return cache.stream().filter(key -> key.getGameServer().equals(gameServer)).findAny().orElse(null);
    }

    public CloudSign getNextFreeSignByTemplate(ITemplate template) {
        return getSignsByTemplate(template).stream().filter(key -> key.getGameServer() == null).findAny().orElse(null);
    }

    public SignExecute getAddSign() {
        return addSign;
    }

    public SignExecute
    getRemoveSign() {
        return removeSign;
    }
}
