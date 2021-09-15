package de.polocloud.modules.smartproxy.pluginside;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.handling.IEventHandler;
import de.polocloud.api.event.impl.other.ProxyConstructPlayerEvent;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.def.SimpleCloudPlayer;
import de.polocloud.api.player.def.SimplePlayerConnection;
import de.polocloud.api.player.extras.IPlayerConnection;
import lombok.var;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.lang.reflect.Field;

public class PlayerInjectListener implements IEventHandler<ProxyConstructPlayerEvent>, Listener {

    public PlayerInjectListener() {
        PoloCloudAPI.getInstance().getEventManager().registerHandler(ProxyConstructPlayerEvent.class, this);
    }

    @EventHandler (priority = -128)
    public void onPlayerHandshakeEvent(PlayerHandshakeEvent event) {
        //this.injectConnection(event.getConnection());
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onPreLoginEvent(PreLoginEvent event) {
        //this.injectConnection(event.getConnection());
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onProxyPingEvent(ProxyPingEvent event) {
        //this.injectConnection(event.getConnection());
    }

    /**
     * Injects a custom ip into this connection
     *
     * @param connection the connection
     */
    private void injectConnection(PendingConnection connection) {

        var address = BungeeBootstrap.getInstance().getAddresses().get(connection.getAddress());

        if (address == null) return;
        try {
            Field wrapperField = connection.getClass().getDeclaredField("ch");
            wrapperField.setAccessible(true);
            Object wrapper = wrapperField.get(connection);
            Field addressField = wrapper.getClass().getDeclaredField("remoteAddress");
            addressField.setAccessible(true);
            addressField.set(wrapper, address);

            if (connection.getName() == null) {
                return;
            }
            ICloudPlayer cloudPlayer = PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(connection.getName());

            IPlayerConnection playerConnection = cloudPlayer.getConnection();

            if (playerConnection != null) {
                ((SimplePlayerConnection)playerConnection).setHost(connection.getAddress().getAddress().getHostAddress());
                ((SimplePlayerConnection)playerConnection).setPort(connection.getAddress().getPort());
                ((SimpleCloudPlayer)cloudPlayer).setConnection(playerConnection);
                cloudPlayer.update();
                System.out.println("UPDATED");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleEvent(ProxyConstructPlayerEvent event) {
        var connection = event.getConnection();
        var address = BungeeBootstrap.getInstance().getAddresses().get(connection.constructAddress());

        if (address == null) {
            System.out.println("Nulled address requested");
            return;
        }

        connection.injectAddress(address);

        var cloudPlayer = new SimpleCloudPlayer(connection.getName(), connection.getUniqueId(), connection);
        cloudPlayer.setProxyServer(PoloCloudAPI.getInstance().getGameServerManager().getThisService().getName());
        cloudPlayer.setConnection(connection);

        event.setResult(cloudPlayer);

    }
}
