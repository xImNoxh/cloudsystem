package de.polocloud.api.config.master.properties;

import de.polocloud.api.config.IConfig;
import de.polocloud.api.fallback.base.SimpleFallback;
import de.polocloud.api.network.protocol.IProtocolObject;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Properties implements IConfig, IProtocolObject {

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
     * If the proxy should be online mode
     * (No online = cracked users)
     */
    private boolean proxyOnlineMode;

    /**
     * Also known as 'proxyProtocol'
     * if ping requests should be forwarded
     * to the host for information access
     */
    private boolean proxyPingForwarding;

    /**
     * Syncs the amount of online players (e.g. 100)
     * To all proxies even if there are only 50 Players
     * on 2 proxies both will display 100 players
     * If its disabled they will display 50 each
     */
    private boolean syncProxyOnlinePlayers;

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

        this.wrapperKey = null;
        this.fallbacks = new ArrayList<>();

        this.maxSimultaneouslyStartingTemplates = 2;
        this.port = 8869;
        this.defaultProxyStartPort = 25565;
        this.defaultServerStartPort = 3000;

        this.logPlayerConnections = false;
        this.proxyOnlineMode = true;
        this.proxyPingForwarding = false;
        this.syncProxyOnlinePlayers = true;
    }

    public void setProxyOnlineMode(boolean proxyOnlineMode) {
        this.proxyOnlineMode = proxyOnlineMode;
    }

    public boolean isProxyOnlineMode() {
        return proxyOnlineMode;
    }

    public boolean isSyncProxyOnlinePlayers() {
        return syncProxyOnlinePlayers;
    }

    public void setSyncProxyOnlinePlayers(boolean syncProxyOnlinePlayers) {
        this.syncProxyOnlinePlayers = syncProxyOnlinePlayers;
    }

    public boolean isProxyPingForwarding() {
        return proxyPingForwarding;
    }

    public void setProxyPingForwarding(boolean proxyPingForwarding) {
        this.proxyPingForwarding = proxyPingForwarding;
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

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(wrapperKey);
        buf.writeBoolean(logPlayerConnections);
        buf.writeInt(maxSimultaneouslyStartingTemplates);
        buf.writeBoolean(proxyOnlineMode);
        buf.writeBoolean(proxyPingForwarding);
        buf.writeBoolean(syncProxyOnlinePlayers);
        buf.writeInt(port);
        buf.writeInt(defaultProxyStartPort);
        buf.writeInt(defaultServerStartPort);

        buf.writeInt(fallbacks.size());
        for (SimpleFallback fallback : fallbacks) {
            buf.writeFallback(fallback);
        }
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        wrapperKey = buf.readString();
        logPlayerConnections = buf.readBoolean();
        maxSimultaneouslyStartingTemplates = buf.readInt();
        proxyOnlineMode = buf.readBoolean();
        proxyPingForwarding = buf.readBoolean();
        syncProxyOnlinePlayers = buf.readBoolean();
        port = buf.readInt();
        defaultProxyStartPort = buf.readInt();
        defaultServerStartPort = buf.readInt();
        int size = buf.readInt();
        fallbacks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            fallbacks.add((SimpleFallback) buf.readFallback());
        }
    }
}
