package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.polocloud.api.config.IConfig;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.master.MasterLoginResponsePacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperLoginPacket;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.WrapperClient;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

public class WrapperLoginPacketHandler extends IPacketHandler {

    @Inject
    private IWrapperClientManager wrapperClientManager;

    @Inject
    private MasterConfig config;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {

        WrapperLoginPacket packet = (WrapperLoginPacket) obj;

        boolean response = config.getLoginKey().equals(packet.getKey());

        Logger.log(LoggerType.INFO, "Wrapper Login attempt > " + response);

        MasterLoginResponsePacket responsePacket = new MasterLoginResponsePacket(response, response ? "Ok" : "Wrong LOGIN_KEY!");
        WrapperClient wrapperClient = new WrapperClient(ctx);
        wrapperClient.sendPacket(responsePacket);

        if (!response) {
            ctx.close();
        } else {
            wrapperClientManager.registerWrapperClient(wrapperClient);
        }

    }

    @Override
    public Class<? extends IPacket> getPacketClass() {
        return WrapperLoginPacket.class;
    }
}
