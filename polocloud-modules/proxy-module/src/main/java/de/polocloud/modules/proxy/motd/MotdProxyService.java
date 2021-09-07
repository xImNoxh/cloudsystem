package de.polocloud.modules.proxy.motd;

import de.polocloud.modules.proxy.bootstrap.ProxyPluginBootstrap;
import de.polocloud.modules.proxy.motd.channel.MotdVersionChannel;
import de.polocloud.modules.proxy.motd.events.ProxyEvents;
import de.polocloud.modules.proxy.motd.properties.MotdVersionProperty;
import net.md_5.bungee.api.ProxyServer;

public class MotdProxyService {

    private static MotdProxyService instance;
    private MotdVersionChannel motdVersionChannel;
    private MotdVersionProperty property;

    public MotdProxyService() {
        instance = this;
        motdVersionChannel = new MotdVersionChannel();
        motdVersionChannel.getChannel().registerListener((globalConfigClassWrappedObject, startTime) -> {
            property = globalConfigClassWrappedObject.unwrap(MotdVersionProperty.class);
        });
        ProxyServer.getInstance().getPluginManager().registerListener(ProxyPluginBootstrap.getInstance(), new ProxyEvents());
    }

    public MotdVersionProperty getProperty() {
        return property;
    }

    public static MotdProxyService getInstance() {
        return instance;
    }

    public MotdVersionChannel getMotdVersionChannel() {
        return motdVersionChannel;
    }
}
