package de.polocloud.modules.proxy;

import com.google.common.collect.Lists;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.module.CloudModule;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.modules.proxy.motd.MotdService;
import de.polocloud.modules.proxy.motd.config.ProxyMotdSettings;
import de.polocloud.modules.proxy.notify.NotifyService;
import de.polocloud.modules.proxy.tablist.config.TablistConfig;
import de.polocloud.modules.proxy.whitelist.WhitelistService;

import java.io.File;
import java.util.List;

public class ProxyModule {

    private static ProxyModule proxyModule;
    private List<IProxyReload> reloaded;

    private MotdService motdService;
    private WhitelistService whitelistService;
    private NotifyService notifyService;

    private CloudModule module;
    private ProxyConfig proxyConfig;

    public ProxyModule(CloudModule module) {

        proxyModule = this;

        this.reloaded = Lists.newArrayList();
        this.module = module;

        loadConfig();
        saveConfig();
    }

    public void enable(){
        reloaded.add(this.motdService = new MotdService());
        reloaded.add(this.whitelistService = new WhitelistService());
        reloaded.add(this.notifyService = new NotifyService());
    }

    public void reload(){
        loadConfig();
        reloaded.forEach(reload -> reload.onReload());
    }

    public void loadConfig(){
        proxyConfig = new SimpleConfigLoader().load(ProxyConfig.class, new File(module.getDataDirectory(), "config.json"));
    }

    public void saveConfig(){
        new SimpleConfigSaver().save(proxyConfig, new File(module.getDataDirectory(), "config.json"));
    }

    public MotdService getMotdService() {
        return motdService;
    }

    public CloudModule getModule() {
        return module;
    }

    public NotifyService getNotifyService() {
        return notifyService;
    }

    public List<IProxyReload> getReloaded() {
        return reloaded;
    }

    public ProxyConfig getProxyConfig() {
        return proxyConfig;
    }

    public WhitelistService getWhitelistService() {
        return whitelistService;
    }

    public static ProxyModule getProxyModule() {
        return proxyModule;
    }
}
