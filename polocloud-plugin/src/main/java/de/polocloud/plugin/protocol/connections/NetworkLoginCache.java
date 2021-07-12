package de.polocloud.plugin.protocol.connections;

import net.md_5.bungee.api.event.LoginEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkLoginCache {

    private Map<UUID, LoginEvent> loginEvents;
    private Map<UUID, String> loginServers;

    public NetworkLoginCache() {
        this.loginEvents = new ConcurrentHashMap<>();
        this.loginServers = new ConcurrentHashMap<>();
    }

    public Map<UUID, LoginEvent> getLoginEvents() {
        return loginEvents;
    }

    public Map<UUID, String> getLoginServers() {
        return loginServers;
    }
}
