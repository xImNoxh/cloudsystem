package de.polocloud.internalwrapper.impl.handler;

import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.packets.master.MasterRequestsServerTerminatePacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.internalwrapper.InternalWrapper;
import de.polocloud.internalwrapper.impl.manager.screen.IScreen;
import de.polocloud.internalwrapper.impl.manager.screen.IScreenManager;
import io.netty.channel.ChannelHandlerContext;

public class WrapperHandlerMasterRequestTerminate implements IPacketHandler<Packet> {

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        MasterRequestsServerTerminatePacket packet = (MasterRequestsServerTerminatePacket) obj;

        long snowflake = packet.getSnowflake();
        String name = packet.getName();

        IScreenManager screenManager = InternalWrapper.getInstance().getScreenManager();

        IScreen screen = screenManager.getScreen(name);
        if (screen != null) {
            Process process = screen.getProcess();
            if (process != null) {
                process.destroy();
            }
        } else {
            PoloLogger.print(LogLevel.ERROR, "§cCouldn't stop §e" + name + " §cbecause no Screen was registered!");
        }
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return MasterRequestsServerTerminatePacket.class;
    }
}
