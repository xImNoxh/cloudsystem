package de.polocloud.bootstrap.network.handler.wrapper;

import com.google.inject.Inject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.network.protocol.packet.master.MasterLoginResponsePacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperLoginPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRegisterStaticServerPacket;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.wrapper.IWrapper;
import de.polocloud.api.wrapper.IWrapperManager;
import de.polocloud.bootstrap.wrapper.SimpleMasterWrapper;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class WrapperHandlerServiceController {

    @Inject
    private ITemplateService tmpService;

    @Inject
    private MasterConfig masterConfig;


    protected final IWrapperManager wrapperManager;

    protected WrapperHandlerServiceController() {
        this.wrapperManager = PoloCloudAPI.getInstance().getWrapperManager();
    }

    public void getTemplateByName(WrapperRegisterStaticServerPacket packet, Consumer<ITemplate> tmp) {
        try {
            tmp.accept(tmpService.getTemplateByName(packet.getTemplateName()).get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public SimpleGameServer createNewService(ChannelHandlerContext ctx, ITemplate template, WrapperRegisterStaticServerPacket packet) {
        return new SimpleGameServer(wrapperManager.getWrapper(ctx), packet.getServerName(), GameServerStatus.PENDING,
            null, packet.getSnowflake(), template, getCurrentMillis(), template.getMotd(), template.getMaxPlayers(), false);
    }

    public MasterLoginResponsePacket getMasterLoginResponsePacket(boolean response) {
        return new MasterLoginResponsePacket(response, response ?
            "Master authentication " + ConsoleColors.GREEN + "successfully " + ConsoleColors.GRAY + "completed."
            : "Master authentication " + ConsoleColors.RED + "denied" + ConsoleColors.GRAY + ".");
    }

    public long getCurrentMillis() {
        return System.currentTimeMillis();
    }

    public void getLoginResponse(WrapperLoginPacket packet, BiConsumer<Boolean, IWrapper> response, ChannelHandlerContext ctx) {
        response.accept(masterConfig.getProperties().getWrapperKey().equals(packet.getKey()), new SimpleMasterWrapper(packet.getName(), ctx));
    }

    public void sendWrapperSuccessfully(WrapperLoginPacket packet) {
        Logger.log(LoggerType.INFO, "The Wrapper " + ConsoleColors.LIGHT_BLUE + packet.getName() + ConsoleColors.GRAY + " is successfully connected to the master.");
    }

}
