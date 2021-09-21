package de.polocloud.modules.hubcommand.pluginside.bootstrap;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import de.polocloud.modules.hubcommand.HubModule;
import de.polocloud.modules.hubcommand.cloudside.ModuleBootstrap;
import lombok.Getter;
import org.slf4j.Logger;

@Plugin(
    id = "hub",
    name = "PoloCloud-HubCommand",
    version = "1.0.0",
    description = "This is the HubCommand module",
    authors = "HttpMarco",
    dependencies = @Dependency(id = "bridge"),
    url = "https://polocloud.de"
) @Getter
public class VelocityBootstrap {

    private final ProxyServer server;
    private final Logger logger;
    private final HubModule hubCommandModule;


    @Inject
    public VelocityBootstrap(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

        hubCommandModule = new HubModule(new ModuleBootstrap());
    }

    @Subscribe
    public void handle(ProxyReloadEvent event) {
    }

    @Subscribe
    public void handle(ProxyShutdownEvent event) {
    }

}

