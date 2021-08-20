package de.polocloud.bootstrap.network;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.network.handler.event.EventPacketHandler;
import de.polocloud.bootstrap.network.handler.gameserver.GameServerPacketServiceHandler;
import de.polocloud.bootstrap.network.handler.player.PlayerPacketHandler;
import de.polocloud.bootstrap.network.handler.template.TemplatePacketHandler;
import de.polocloud.bootstrap.network.handler.wrapper.WrapperPacketHandlerService;

public class SimplePacketService {

    public SimplePacketService() {

        PoloCloudAPI.getInstance().getGuice().getInstance(PlayerPacketHandler.class);
        PoloCloudAPI.getInstance().getGuice().getInstance(GameServerPacketServiceHandler.class);
        new EventPacketHandler();
        PoloCloudAPI.getInstance().getGuice().getInstance(WrapperPacketHandlerService.class);
        PoloCloudAPI.getInstance().getGuice().getInstance(TemplatePacketHandler.class);

        registerHandler();

    }

    public void registerHandler() {
        SimplePacketHandler.LISTENING.forEach(packets -> Master.getInstance().getNettyServer().getProtocol().registerPacketHandler(packets));
    }

}
