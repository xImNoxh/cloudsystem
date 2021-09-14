package de.polocloud.modules.smartproxy.moduleside.minecraft.netty;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.modules.smartproxy.moduleside.SmartProxy;
import de.polocloud.modules.smartproxy.moduleside.minecraft.netty.packet.MinecraftPacket;
import de.polocloud.modules.smartproxy.moduleside.minecraft.netty.packet.MinecraftPacketBuffer;
import de.polocloud.modules.smartproxy.moduleside.minecraft.netty.packet.MinecraftPingPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;


@ChannelHandler.Sharable
public class PacketDecoder extends SimpleChannelInboundHandler<ByteBuf> {

    public PacketDecoder() {
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {

            InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();

            MinecraftPacketBuffer buffer = new MinecraftPacketBuffer(buf);
            int packetLength = buffer.readSignedVarInt();
            int packetID = buffer.readSignedVarInt();

            Class<? extends MinecraftPacket> aClass = SmartProxy.MINECRAFT_PACKETS.get(packetID);

            if (aClass == null) {
                //CloudDriver.getInstance().log("de.polocloud.modules.smartproxy.moduleside.SmartProxy", "§cReceived Packet with id §e" + packetID + " §cthat is not registered!");
                return;
            }
            MinecraftPacket minecraftPacket = aClass.newInstance();

            minecraftPacket.read(buffer);
            minecraftPacket.handle();
            buf.retain();

            if (minecraftPacket instanceof MinecraftPingPacket) {
                MinecraftPingPacket pingPacket = (MinecraftPingPacket) minecraftPacket;

                String hostName = pingPacket.getHostName();

                hostName = hostName.replace("localhost", "127.0.0.1");
                hostName = hostName.replace("192.168.178.82", "127.0.0.1");

                List<IGameServer> proxies = PoloCloudAPI.getInstance().getGameServerManager().getAllCached(TemplateType.PROXY);
                IGameServer service = proxies.get(new Random().nextInt(proxies.size()));

                if (service != null) {
                    IGameServer freeProxy = SmartProxy.getInstance().getFreeProxy(service.getTemplate(), pingPacket.getState());
                    if (freeProxy == null) {
                        PoloLogger.print(LogLevel.ERROR, "§cNo free Proxy for group §e" + service.getTemplate().getName() + " could be found. Shutting down...");
                        ctx.channel().close();
                        return;
                    }
                    SmartProxy.getInstance().forwardRequestToNextProxy(ctx.channel(), freeProxy, buf, pingPacket.getState());
                }
            }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //Ignoring exceptions at first
    }

}
