package de.polocloud.bootstrap.network;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.network.handler.other.*;
import de.polocloud.bootstrap.network.handler.gameserver.GameServerPacketServiceHandler;
import de.polocloud.bootstrap.network.handler.player.PlayerPacketHandler;
import de.polocloud.bootstrap.network.handler.property.PropertyDeletePacketHandler;
import de.polocloud.bootstrap.network.handler.wrapper.WrapperPacketHandlerService;

public class SimplePacketService {

    public SimplePacketService() {

        new PlayerPacketHandler();
        new GameServerPacketServiceHandler();

        PoloCloudAPI.getInstance().getConnection().getProtocol().registerPacketHandler(new WrapperPacketHandlerService());


        PoloCloudAPI.getInstance().getConnection().getProtocol().registerPacketHandler(new EventPacketHandler());
        PoloCloudAPI.getInstance().getConnection().getProtocol().registerPacketHandler(new ExceptionReportPacketHandler());
        PoloCloudAPI.getInstance().getConnection().getProtocol().registerPacketHandler(new TextPacketHandler());
        PoloCloudAPI.getInstance().getConnection().getProtocol().registerPacketHandler(new ConfigPacketHandler());
        PoloCloudAPI.getInstance().getConnection().getProtocol().registerPacketHandler(new ShutdownPacketHandler());
        PoloCloudAPI.getInstance().getConnection().getProtocol().registerPacketHandler(new CacheUpdatePacketHandler());

        registerHandler();

    }

    public void registerHandler() {
        SimplePacketHandler.LISTENING.forEach(packets -> Master.getInstance().getNettyServer().getProtocol().registerPacketHandler(packets));
    }

}
