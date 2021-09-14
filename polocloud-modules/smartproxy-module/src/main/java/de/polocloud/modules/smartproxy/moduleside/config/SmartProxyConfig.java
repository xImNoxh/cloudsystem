package de.polocloud.modules.smartproxy.moduleside.config;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.IConfig;
import de.polocloud.modules.smartproxy.moduleside.SmartProxy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter @Setter @AllArgsConstructor
public class SmartProxyConfig implements IConfig {

    /**
     * The searching mode for a free proxy
     */
    private String proxySearchMode;

    /**
     * IF enabled
     */
    private boolean enabled;

    public SmartProxyConfig() {
        this.proxySearchMode = "RANDOM";
        this.enabled = true;
    }

    /**
     * Updates this config
     */
    public void update() {
        PoloCloudAPI.getInstance().getConfigSaver().save(this, new File(SmartProxy.getInstance().getDataDirectory(), "module.json"));
    }
}
