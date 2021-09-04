package de.polocloud.bootstrap.network;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.network.handler.other.EventPacketHandler;
import de.polocloud.bootstrap.network.handler.gameserver.GameServerPacketServiceHandler;
import de.polocloud.bootstrap.network.handler.other.ShutdownPacketHandler;
import de.polocloud.bootstrap.network.handler.other.TextPacketHandler;
import de.polocloud.bootstrap.network.handler.player.PlayerPacketHandler;
import de.polocloud.bootstrap.network.handler.property.PropertyDeletePacketHandler;
import de.polocloud.bootstrap.network.handler.property.PropertyInsertPacketHandler;
import de.polocloud.bootstrap.network.handler.wrapper.WrapperPacketHandlerService;

public class SimplePacketService {

    public SimplePacketService() {

        PoloCloudAPI.getInstance().getGuice().getInstance(PlayerPacketHandler.class);
        PoloCloudAPI.getInstance().getGuice().getInstance(GameServerPacketServiceHandler.class);
        PoloCloudAPI.getInstance().getGuice().getInstance(WrapperPacketHandlerService.class);

        PoloCloudAPI.getInstance().getConnection().getProtocol().registerPacketHandler(new PropertyDeletePacketHandler());
        PoloCloudAPI.getInstance().getConnection().getProtocol().registerPacketHandler(new PropertyInsertPacketHandler());

        PoloCloudAPI.getInstance().getConnection().getProtocol().registerPacketHandler(new EventPacketHandler());
        PoloCloudAPI.getInstance().getConnection().getProtocol().registerPacketHandler(new TextPacketHandler());
        PoloCloudAPI.getInstance().getConnection().getProtocol().registerPacketHandler(new ShutdownPacketHandler());

        registerHandler();

    }

    public void registerHandler() {
        SimplePacketHandler.LISTENING.forEach(packets -> Master.getInstance().getNettyServer().getProtocol().registerPacketHandler(packets));
    }

}
