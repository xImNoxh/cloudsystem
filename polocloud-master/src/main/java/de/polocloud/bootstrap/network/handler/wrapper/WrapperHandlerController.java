package de.polocloud.bootstrap.network.handler.wrapper;

import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.ServiceVisibility;
import de.polocloud.api.network.protocol.packet.master.MasterLoginResponsePacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperLoginPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRegisterStaticServerPacket;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import de.polocloud.logger.log.types.ConsoleColors;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public abstract class WrapperHandlerController {

    public void getTemplateByName(ITemplateService service, WrapperRegisterStaticServerPacket packet, Consumer<ITemplate> tmp) {
        try {
            tmp.accept(service.getTemplateByName(packet.getTemplateName()).get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public SimpleGameServer createNewService(IWrapperClientManager client, ChannelHandlerContext ctx, ITemplate template, WrapperRegisterStaticServerPacket packet) {
        return new SimpleGameServer(client.getWrapperClientByConnection(ctx), packet.getServerName(), GameServerStatus.PENDING,
            null, packet.getSnowflake(), template, getCurrentMillis(), template.getMotd(), template.getMaxPlayers(), ServiceVisibility.INVISIBLE);
    }

    public MasterLoginResponsePacket getMasterLoginResponsePacket(boolean response) {
        return new MasterLoginResponsePacket(response, response ?
            "Master authentication " + ConsoleColors.GREEN + "successfully " + ConsoleColors.GRAY + "completed."
            : "Master authentication " + ConsoleColors.RED + "denied" + ConsoleColors.GRAY + ".");
    }

    public long getCurrentMillis() {
        return System.currentTimeMillis();
    }

    public void getLoginResponse(MasterConfig config, WrapperLoginPacket packet, Consumer<Boolean> response) {
        response.accept(config.getProperties().getWrapperKey().equals(packet.getKey()));
    }


}
