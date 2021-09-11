package de.polocloud.modules.proxy;

import com.google.common.collect.Lists;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.module.CloudModule;
import de.polocloud.modules.proxy.motd.MotdService;
import de.polocloud.modules.proxy.notify.NotifyService;
import de.polocloud.modules.proxy.tablist.TablistService;
import de.polocloud.modules.proxy.whitelist.WhitelistService;

import java.io.File;
import java.util.List;

public class ProxyModule {

    private static ProxyModule proxyModule;
    private List<IProxyReload> reloaded;

    private MotdService motdService;
    private WhitelistService whitelistService;
    private NotifyService notifyService;
    private TablistService tablistService;

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
        reloaded.add(this.tablistService = new TablistService());
    }

    public void reload(){
        loadConfig();
        reloaded.forEach(IProxyReload::onReload);
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
