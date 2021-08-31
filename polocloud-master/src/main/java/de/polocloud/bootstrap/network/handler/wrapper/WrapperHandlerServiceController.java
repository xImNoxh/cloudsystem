package de.polocloud.bootstrap.network.handler.wrapper;

import com.google.inject.Inject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.SimpleGameServer;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.network.packets.master.MasterLoginResponsePacket;
import de.polocloud.api.network.packets.wrapper.WrapperLoginPacket;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.ITemplateManager;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.api.wrapper.IWrapperManager;
import de.polocloud.bootstrap.wrapper.SimpleMasterWrapper;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.api.logger.helper.LogLevel;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class WrapperHandlerServiceController {

    @Inject
    private ITemplateManager tmpService;

    @Inject
    private MasterConfig masterConfig;


    protected final IWrapperManager wrapperManager;

    protected WrapperHandlerServiceController() {
        this.wrapperManager = PoloCloudAPI.getInstance().getWrapperManager();
    }


    public MasterLoginResponsePacket getMasterLoginResponsePacket(boolean response) {
        return new MasterLoginResponsePacket(response, response ?
            "Master authentication §asuccessfully §7completed."
            : "Master authentication §cdenied§7!");
    }

    public long getCurrentMillis() {
        return System.currentTimeMillis();
    }

    public void getLoginResponse(WrapperLoginPacket packet, BiConsumer<Boolean, IWrapper> response, ChannelHandlerContext ctx) {
        response.accept(masterConfig.getProperties().getWrapperKey().equals(packet.getKey()), new SimpleMasterWrapper(packet.getName(), ctx));
    }

    public void sendWrapperSuccessfully(WrapperLoginPacket packet) {
        PoloLogger.print(LogLevel.INFO, "The Wrapper " + ConsoleColors.LIGHT_BLUE + packet.getName() + ConsoleColors.GRAY + " is successfully connected to the master.");
    }

}
