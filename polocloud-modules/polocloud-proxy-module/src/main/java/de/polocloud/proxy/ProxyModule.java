package de.polocloud.proxy;

import de.polocloud.api.module.Module;
import de.polocloud.proxy.config.ProxyConfig;

import java.io.File;

public class ProxyModule {

    private ProxyConfig proxyConfig;

    public ProxyModule(Module module) {

        this.proxyConfig = loadProxyConfig(module);

    }

    public ProxyConfig loadProxyConfig(Module module) {
        File configPath = new File("modules/proxy/");
        if (!configPath.exists()) configPath.mkdirs();

        File configFile = new File("modules/proxy/config.json");

        ProxyConfig tablistConfig = module.getConfigLoader().load(ProxyConfig.class, configFile);
        module.getConfigSaver().save(tablistConfig, configFile);
        return tablistConfig;
    }


}
