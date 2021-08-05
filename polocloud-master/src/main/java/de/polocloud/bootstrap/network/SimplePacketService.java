package de.polocloud.bootstrap.network;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.network.handler.gameserver.GameServerHandler;
import de.polocloud.bootstrap.network.handler.player.PlayerPacketHandler;
import de.polocloud.bootstrap.network.handler.wrapper.WrapperPacketHandler;

public class SimplePacketService {

    public SimplePacketService() {

        PoloCloudAPI.getInstance().getGuice().getInstance(PlayerPacketHandler.class);
        PoloCloudAPI.getInstance().getGuice().getInstance(GameServerHandler.class);
        PoloCloudAPI.getInstance().getGuice().getInstance(WrapperPacketHandler.class);

        registerHandler();

    }

    public void registerHandler() {
        SimplePacketHandler.LISTENING.forEach(packets -> Master.getInstance().getNettyServer().getProtocol().registerPacketHandler(packets));
    }

}
