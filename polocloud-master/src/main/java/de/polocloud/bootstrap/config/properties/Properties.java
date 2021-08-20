package de.polocloud.bootstrap.config.properties;

import com.google.common.collect.Lists;
import de.polocloud.api.config.IConfig;
import de.polocloud.api.fallback.base.SimpleFallback;

import java.util.LinkedList;

public class Properties implements IConfig {

    private String wrapperKey = "--Polo--";
    private boolean logPlayerConnections = true;
    private int maxSimultaneouslyStartingTemplates = 2;
    private int port = 8869;
    private int defaultProxyStartPort = 25565;
    private LinkedList<SimpleFallback> fallbackProperties = Lists.newLinkedList();

    public int getPort() {
        return port;
    }

    public boolean isLogPlayerConnections() {
        return logPlayerConnections;
    }

    public int getMaxSimultaneouslyStartingTemplates() {
        return maxSimultaneouslyStartingTemplates;
    }

    public String getWrapperKey() {
        return wrapperKey;
    }

    public LinkedList<SimpleFallback> getFallbackProperties() {
        return fallbackProperties;
    }

    public int getDefaultProxyStartPort() {
        return defaultProxyStartPort;
    }
}
