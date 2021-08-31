package de.polocloud.wrapper.impl.handler;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.alias.CloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.packets.master.MasterRequestServerStartPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.wrapper.Wrapper;
import de.polocloud.wrapper.manager.server.ServiceStarter;
import io.netty.channel.ChannelHandlerContext;

public class WrapperHandlerMasterRequestStart implements IPacketHandler<Packet> {


    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {

        MasterRequestServerStartPacket packet = (MasterRequestServerStartPacket) obj;
        this.tryStart(packet.getServerName(), packet.getPort());
    }


    private void tryStart(String name, int port) {

        IGameServer cached = CloudAPI.getInstance().getGameServerManager().getCached(name);
        if (cached != null) {
            if (cached.getPort() == -1 && port == -1) {
                cached.setPort(PoloCloudAPI.getInstance().getPortManager().getPort(cached.getTemplate()));
            } else if (port != -1) {
                cached.setPort(port);
            }
            ServiceStarter serviceStarter = new ServiceStarter(cached);

            try {
                serviceStarter.copyFiles();
                serviceStarter.createProperties();
                serviceStarter.createCloudFiles();
                serviceStarter.start(server -> {
                    //If in screen not sending message!
                    if (Wrapper.getInstance().getScreenManager().getScreen() != null && Wrapper.getInstance().getScreenManager().isInScreen()) {
                        return;
                    }
                    PoloLogger.print(LogLevel.INFO, "§7Queued GameServer §b" + server.getName() + " §7[§7Port: §b" + server.getPort() + " §7| §7Mode: §b" + (server.getTemplate().isDynamic() ? "DYNAMIC" : "STATIC") + "_" + server.getTemplate().getTemplateType() + "§7]");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            PoloLogger.print(LogLevel.ERROR, "§cTried to start GameServer §e" + name + " §calthough it was not registered before!");
            PoloLogger.print(LogLevel.INFO, "§cRequesting Cache and trying again in §e1 second§c...");
            PoloCloudAPI.getInstance().updateCache();
            Scheduler.runtimeScheduler().schedule(() -> this.tryStart(name, port), 20L);
        }

    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return MasterRequestServerStartPacket.class;
    }
}
