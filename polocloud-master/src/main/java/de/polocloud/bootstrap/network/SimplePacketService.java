package de.polocloud.bootstrap.network;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.network.handler.player.PlayerPacketHandler;

public class SimplePacketService {

    public SimplePacketService() {

        PoloCloudAPI.getInstance().getGuice().getInstance(PlayerPacketHandler.class);

        registerHandler();

    }

    public void registerHandler() {
        SimplePacketHandler.LISTENING.forEach(packets -> Master.getInstance().getNettyServer().getProtocol().registerPacketHandler(packets));
    }

}
