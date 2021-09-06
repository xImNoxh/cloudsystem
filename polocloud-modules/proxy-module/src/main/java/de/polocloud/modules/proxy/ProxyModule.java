package de.polocloud.modules.proxy;

import com.google.common.collect.Lists;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.module.CloudModule;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.modules.proxy.motd.MotdService;
import de.polocloud.modules.proxy.motd.config.ProxyMotdSettings;
import de.polocloud.modules.proxy.tablist.config.TablistConfig;

import java.io.File;
import java.util.List;

public class ProxyModule {

    private static ProxyModule proxyModule;
    private List<IProxyReload> reloaded;

    private MotdService motdService;

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
    }

    public void reload(){
        loadConfig();

        reloaded.forEach(reload -> reload.onReload());
    }

    public void loadConfig(){
        proxyConfig = new SimpleConfigLoader().load(ProxyConfig.class, new File(module.getDataDirectory(), "config.yml"));
    }

    public void saveConfig(){
        new SimpleConfigSaver().save(proxyConfig, new File(module.getDataDirectory(), "config.yml"));
    }

    public List<IProxyReload> getReloaded() {
        return reloaded;
    }

    public ProxyConfig getProxyConfig() {
        return proxyConfig;
    }

    public static ProxyModule getProxyModule() {
        return proxyModule;
    }
}
