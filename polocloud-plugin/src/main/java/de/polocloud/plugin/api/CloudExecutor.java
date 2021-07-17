package de.polocloud.plugin.api;

import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.IPacketSender;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.pubsub.SimplePubSubManager;
import de.polocloud.plugin.api.player.APICloudPlayerManager;
import de.polocloud.plugin.api.server.APIGameServerManager;

public class CloudExecutor {

    private static CloudExecutor instance;

    private IPubSubManager pubSubManager;
    private IGameServerManager gameServerManager = new APIGameServerManager();
    private ICloudPlayerManager cloudPlayerManager = new APICloudPlayerManager();

    public CloudExecutor(IPacketSender sender, IProtocol protocol) {
        instance = this;
        this.pubSubManager = new SimplePubSubManager(sender, protocol);
    }



    public IPubSubManager getPubSubManager() {
        return pubSubManager;
    }

    public static CloudExecutor getInstance() {
        return instance;
    }

    public ICloudPlayerManager getCloudPlayerManager() {
        return cloudPlayerManager;
    }

    public IGameServerManager getGameServerManager() {
        return gameServerManager;
    }
}
