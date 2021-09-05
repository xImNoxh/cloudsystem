package de.polocloud.api.config.master.properties;

import de.polocloud.api.config.IConfig;
import de.polocloud.api.fallback.base.SimpleFallback;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Properties implements IConfig {

    /**
     * The wrapper key to verify logins
     */
    private String wrapperKey;

    /**
     * If player connections should be displayed in console
     */
    private boolean logPlayerConnections;

    /**
     * ???
     */
    private int maxSimultaneouslyStartingTemplates;

    /**
     * The port of the cloud server
     */
    private int port;

    /**
     * The default port for proxies
     */
    private int defaultProxyStartPort;


    /**
     * The default port for servers
     */
    private int defaultServerStartPort;

    /**
     * All fallbacks
     */
    private List<SimpleFallback> fallbacks;

    public Properties() {

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < ThreadLocalRandom.current().nextInt(5); i++) {
            stringBuilder.append(UUID.randomUUID()).append("#");
        }
        stringBuilder.append("@PoloCloud");

        this.wrapperKey = stringBuilder.toString();
        this.logPlayerConnections = false;
        this.maxSimultaneouslyStartingTemplates = 2;
        this.port = 8869;
        this.defaultProxyStartPort = 25565;
        this.defaultServerStartPort = 3000;
        this.fallbacks = new ArrayList<>();
    }

    public void setWrapperKey(String wrapperKey) {
        this.wrapperKey = wrapperKey;
    }

    public void setLogPlayerConnections(boolean logPlayerConnections) {
        this.logPlayerConnections = logPlayerConnections;
    }

    public void setMaxSimultaneouslyStartingTemplates(int maxSimultaneouslyStartingTemplates) {
        this.maxSimultaneouslyStartingTemplates = maxSimultaneouslyStartingTemplates;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDefaultProxyStartPort(int defaultProxyStartPort) {
        this.defaultProxyStartPort = defaultProxyStartPort;
    }

    public void setDefaultServerStartPort(int defaultServerStartPort) {
        this.defaultServerStartPort = defaultServerStartPort;
    }

    public void setFallbacks(List<SimpleFallback> fallbacks) {
        this.fallbacks = fallbacks;
    }

    public String getWrapperKey() {
        return wrapperKey;
    }

    public boolean isLogPlayerConnections() {
        return logPlayerConnections;
    }

    public int getMaxSimultaneouslyStartingTemplates() {
        return maxSimultaneouslyStartingTemplates;
    }

    public int getPort() {
        return port;
    }

    public int getDefaultProxyStartPort() {
        return defaultProxyStartPort;
    }

    public int getDefaultServerStartPort() {
        return defaultServerStartPort;
    }

    public List<SimpleFallback> getFallbacks() {
        return fallbacks;
    }
}
