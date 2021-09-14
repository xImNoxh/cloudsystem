package de.polocloud.modules.smartproxy.pluginside;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.api.messaging.IMessageListener;
import de.polocloud.api.util.WrappedObject;
import de.polocloud.modules.smartproxy.api.IpSetter;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@Getter
public class BungeeBootstrap extends Plugin {

    @Getter
    private static BungeeBootstrap instance;

    private final Map<InetSocketAddress, InetSocketAddress> addresses;

    private final IMessageChannel<IpSetter> messageChannel;

    public BungeeBootstrap() {
        instance = this;
        this.addresses = new HashMap<>();
        this.messageChannel = PoloCloudAPI.getInstance().getMessageManager().registerChannel(IpSetter.class, "smart-proxy-ipsetting");

        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerInjectListener());

        this.messageChannel.registerListener((wrappedObject, startTime) -> {
            IpSetter unwrap = wrappedObject.unwrap(IpSetter.class);
            addresses.put(unwrap.getChannelAddress(), unwrap.getClientAddress());
        });
    }

    @Override
    public void onEnable() {

    }
}
