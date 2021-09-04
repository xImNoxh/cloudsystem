package de.polocloud.bootstrap.network.handler.player;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.impl.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.impl.player.CloudPlayerJoinNetworkEvent;
import de.polocloud.api.logger.helper.LogLevel;

import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.pubsub.MasterPubSubManager;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.logger.log.types.ConsoleColors;

public abstract class PlayerPacketServiceController {

    public void callConnectEvent(MasterPubSubManager pubSubManager, ICloudPlayer cloudPlayer) {
        pubSubManager.publish("polo:event:playerJoin", cloudPlayer.getName());
        PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerJoinNetworkEvent(cloudPlayer));
    }

    public void callDisconnectEvent(MasterPubSubManager pubSubManager, ICloudPlayer cloudPlayer) {
        pubSubManager.publish("polo:event:playerQuit", cloudPlayer.getUUID().toString());
        PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerDisconnectEvent(cloudPlayer));
    }

    public void sendConnectMessage(MasterConfig masterConfig, ICloudPlayer cloudPlayer) {
        if (masterConfig.getProperties().isLogPlayerConnections() && cloudPlayer != null && cloudPlayer.getProxyServer() != null)
            PoloLogger.print(LogLevel.INFO, "Player " + ConsoleColors.CYAN + cloudPlayer.getName() + ConsoleColors.GRAY +
                " is connected on " + cloudPlayer.getProxyServer().getName() + "!");
    }

    public void sendDisconnectMessage(MasterConfig masterConfig, ICloudPlayer cloudPlayer) {
        if (masterConfig.getProperties().isLogPlayerConnections())
            PoloLogger.print(LogLevel.INFO, "Player " + ConsoleColors.CYAN + cloudPlayer.getName() + ConsoleColors.GRAY + " is now disconnected!");
    }

}
