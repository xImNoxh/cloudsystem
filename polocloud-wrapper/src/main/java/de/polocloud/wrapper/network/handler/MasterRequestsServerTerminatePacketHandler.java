package de.polocloud.wrapper.network.handler;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.master.MasterRequestsServerTerminatePacket;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.wrapper.process.ProcessManager;
import io.netty.channel.ChannelHandlerContext;

public class MasterRequestsServerTerminatePacketHandler implements IPacketHandler<Packet> {

    private ProcessManager processManager;

    public MasterRequestsServerTerminatePacketHandler(ProcessManager processManager) {
        this.processManager = processManager;
    }

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        MasterRequestsServerTerminatePacket packet = (MasterRequestsServerTerminatePacket) obj;

        long snowflake = packet.getSnowflake();
        String name = packet.getName();
        processManager.terminateProcess(snowflake);
        Logger.log(LoggerType.INFO, "§7Server §3" + name + " §7[§b#" + snowflake + "§7] terminated!");
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return MasterRequestsServerTerminatePacket.class;
    }
}
