package de.polocloud.wrapper.network.handler;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.master.MasterRequestsServerTerminatePacket;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.wrapper.process.ProcessManager;
import io.netty.channel.ChannelHandlerContext;

public class MasterRequestsServerTerminatePacketHandler extends IPacketHandler<Packet> {

    private ProcessManager processManager;

    public MasterRequestsServerTerminatePacketHandler(ProcessManager processManager) {
        this.processManager = processManager;
    }

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        MasterRequestsServerTerminatePacket packet = (MasterRequestsServerTerminatePacket) obj;

        long snowflake = packet.getSnowflake();
        processManager.terminateProcess(snowflake);
        Logger.log(LoggerType.INFO, "Process " + snowflake + " Terminated!");


    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return MasterRequestsServerTerminatePacket.class;
    }
}
