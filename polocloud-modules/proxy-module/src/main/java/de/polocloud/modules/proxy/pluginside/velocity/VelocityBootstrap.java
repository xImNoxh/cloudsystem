package de.polocloud.modules.proxy.pluginside.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.polocloud.modules.proxy.ProxyModule;
import de.polocloud.modules.proxy.cloudside.ModuleBootstrap;
import lombok.Getter;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Plugin(
    id = "proxy",
    name = "PoloCloud-Proxy",
    version = "1.0.0",
    description = "This is the communication between cloud and proxy",
    authors = "Lystx",
    dependencies = @Dependency(id = "bridge"),
    url = "https://polocloud.de"
) @Getter
public class VelocityBootstrap {

    private final ProxyServer server;
    private final Logger logger;
    private final ProxyModule proxyModule;

    @Getter
    private static VelocityBootstrap instance;

    @Inject
    public VelocityBootstrap(ProxyServer server, Logger logger) {
        instance = this;
        this.server = server;
        this.logger = logger;

        proxyModule = new ProxyModule(new ModuleBootstrap());
        proxyModule.enable();
    }

    @Subscribe
    public void handle(ProxyReloadEvent event) {
        this.proxyModule.reload();
    }

    @Subscribe
    public void handle(ProxyShutdownEvent event) {
        this.proxyModule.shutdown();
    }

}
