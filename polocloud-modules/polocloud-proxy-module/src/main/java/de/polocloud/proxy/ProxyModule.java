package de.polocloud.proxy;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.gameserver.CloudGameServerMaintenanceUpdateEvent;
import de.polocloud.api.event.gameserver.CloudGameServerStatusChangeEvent;
import de.polocloud.api.module.Module;
import de.polocloud.proxy.cache.ProxyGameServerCache;
import de.polocloud.proxy.collectives.GameServerChangeMaintenanceListener;
import de.polocloud.proxy.collectives.GameServerStatusUpdateListener;
import de.polocloud.proxy.config.ProxyConfig;
import de.polocloud.proxy.info.MotdInfoService;

import java.io.File;

public class ProxyModule {

    private static ProxyModule instance;

    private ProxyConfig proxyConfig;
    private ProxyGameServerCache cache;

    private MotdInfoService motdInfoService;

    public ProxyModule(Module module) {

        instance = this;

        this.proxyConfig = loadProxyConfig(module);
        this.cache = new ProxyGameServerCache();
        this.motdInfoService = new MotdInfoService();

        EventRegistry.registerListener(PoloCloudAPI.getInstance().getGuice().getInstance(GameServerStatusUpdateListener.class), CloudGameServerStatusChangeEvent.class);
        EventRegistry.registerListener(PoloCloudAPI.getInstance().getGuice().getInstance(GameServerChangeMaintenanceListener.class), CloudGameServerMaintenanceUpdateEvent.class);
    }

    public ProxyConfig loadProxyConfig(Module module) {
        File configPath = new File("modules/proxy/");
        if (!configPath.exists()) configPath.mkdirs();

        File configFile = new File("modules/proxy/config.json");

        ProxyConfig tablistConfig = module.getConfigLoader().load(ProxyConfig.class, configFile);
        module.getConfigSaver().save(tablistConfig, configFile);
        return tablistConfig;
    }

    public MotdInfoService getMotdInfoService() {
        return motdInfoService;
    }

    public static ProxyModule getInstance() {
        return instance;
    }

    public ProxyConfig getProxyConfig() {
        return proxyConfig;
    }

    public ProxyGameServerCache getCache() {
        return cache;
    }
}
