package de.polocloud.modules.smartproxy.moduleside;

import de.polocloud.api.config.master.MasterConfig;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.api.module.CloudModule;
import de.polocloud.api.module.ModuleCopyType;
import de.polocloud.api.module.info.ModuleInfo;
import de.polocloud.api.module.info.ModuleState;
import de.polocloud.api.module.info.ModuleTask;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.modules.smartproxy.api.IpSetter;
import de.polocloud.modules.smartproxy.moduleside.command.SmartProxyCommand;
import de.polocloud.modules.smartproxy.moduleside.config.SmartProxyConfig;
import de.polocloud.modules.smartproxy.moduleside.minecraft.MinecraftState;
import de.polocloud.modules.smartproxy.moduleside.minecraft.netty.ProxyNettyServer;
import de.polocloud.modules.smartproxy.moduleside.minecraft.netty.packet.MinecraftPacket;
import de.polocloud.modules.smartproxy.moduleside.minecraft.netty.packet.MinecraftPingPacket;
import de.polocloud.modules.smartproxy.moduleside.minecraft.proxy.ForwardDownStream;
import de.polocloud.modules.smartproxy.moduleside.minecraft.proxy.ForwardUpStream;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static de.polocloud.api.logger.helper.LogLevel.INFO;

@ModuleInfo(
        name = "PoloCloud-SmartProxy",
        authors = "Lystx",
        description = "This is a module to send players to proxies depending on the online count",
        version = "1.0",
        main = SmartProxy.class,
        copyTypes = ModuleCopyType.PROXIES
)

@Getter @Setter
public class SmartProxy extends CloudModule {

    /**
     * The config
     */
    private SmartProxyConfig smartProxyConfig;

    /**
     * If module is enabled
     */
    private boolean enabled;

    /**
     * All registered {@link MinecraftPacket}s
     */
    public static final Map<Integer, Class<? extends MinecraftPacket>> MINECRAFT_PACKETS = new HashMap<>();

    /**
     * The connection states
     */
    public final static AttributeKey<MinecraftState> CONNECTION_STATE = AttributeKey.valueOf("connectionstate");

    /**
     * The {@link ForwardDownStream}s
     */
    public final static AttributeKey<ForwardDownStream> FORWARDING_DOWN = AttributeKey.valueOf("downstreamhandler");

    /**
     * The {@link ForwardUpStream}s
     */
    public final static AttributeKey<ForwardUpStream> FORWARDING_UP = AttributeKey.valueOf("upstreamhandler");

    /**
     * The netty server
     */
    private ProxyNettyServer proxyNettyServer;

    /**
     * The netty channel
     */
    private Channel channel;

    /**
     * The netty worker group
     */
    private EventLoopGroup workerGroup;

    private IMessageChannel<IpSetter> messageChannel;
    /**
     * The static instance
     */
    @Getter
    private static SmartProxy instance;

    @ModuleTask(id = 1, state = ModuleState.LOADING)
    public void loadConfig() {
        instance = this;

        this.smartProxyConfig = PoloCloudAPI.getInstance().getConfigLoader().load(SmartProxyConfig.class, new File(dataDirectory, "module.json"));

        //Default config
        if (this.smartProxyConfig == null || smartProxyConfig.getProxySearchMode() == null) {
            this.smartProxyConfig = new SmartProxyConfig();
            this.smartProxyConfig.update();
        }

        this.messageChannel = PoloCloudAPI.getInstance().getMessageManager().registerChannel(IpSetter.class, "smart-proxy-ipsetting");

        MasterConfig networkConfig = PoloCloudAPI.getInstance().getMasterConfig();

        if (smartProxyConfig.isEnabled()) {
            if (networkConfig.getProperties().getDefaultProxyStartPort() == 25565) {
                PoloLogger.print(INFO, "§7Default-Proxy-Port was §b25565 §7had to change to §325566 §7in order to make §bSmartProxy §7work§7!");

                networkConfig.getProperties().setDefaultProxyStartPort(25566);
                networkConfig.update();
            }

            MINECRAFT_PACKETS.put(0x00, MinecraftPingPacket.class);
        } else {
            if (networkConfig.getProperties().getDefaultProxyStartPort() != 25565) {
                PoloLogger.print(INFO,"§7SmartProxy is currently §cdisabled§7! §7Setting Default-Proxy-Port §7back to §b25565§7!");

                networkConfig.getProperties().setDefaultProxyStartPort(25565);
                networkConfig.update();
            }
        }

        networkConfig.update();
    }

    @ModuleTask(id = 2, state = ModuleState.STARTING)
    public void startModule() {
        if (this.smartProxyConfig.isEnabled()) {
            this.workerGroup = new NioEventLoopGroup();
            this.proxyNettyServer = new ProxyNettyServer("127.0.0.1", 25565);
            PoloCloudAPI.getInstance().getCommandManager().registerCommand(new SmartProxyCommand());
            try {
                proxyNettyServer.bind();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @ModuleTask(id = 3, state = ModuleState.STOPPING)
    public void stopModule() {
        this.proxyNettyServer.unbind();
    }

    /**
     * Forwards a ping request to a {@link IGameServer} and connects the requester to it
     *
     * @param state the login state
     * @param channel the netty channel
     * @param proxy the proxy
     * @param login the login packet as buf
     */
    public void forwardRequestToNextProxy(Channel channel, IGameServer proxy, ByteBuf login, int state) {
        ForwardDownStream downstreamHandler = channel.attr(SmartProxy.FORWARDING_DOWN).get() == null ? new ForwardDownStream(channel) : channel.attr(SmartProxy.FORWARDING_DOWN).get();
        channel.attr(SmartProxy.FORWARDING_DOWN).set(downstreamHandler);
        channel.attr(SmartProxy.CONNECTION_STATE).set(MinecraftState.HANDSHAKE);

        new Bootstrap()
                .group(this.workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    public void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(downstreamHandler);
                    }
                }).connect(proxy.getAddress()).addListener((ChannelFutureListener) channelFuture -> {
                    if (channelFuture.isSuccess()) {

                        if (channel.attr(SmartProxy.FORWARDING_UP).get() == null) {
                            ForwardUpStream upstreamHandler = new ForwardUpStream(channelFuture.channel(), downstreamHandler);
                            channel.pipeline().addLast(upstreamHandler);
                            channel.attr(SmartProxy.FORWARDING_UP).set(upstreamHandler);
                        } else {
                            channel.attr(SmartProxy.FORWARDING_UP).get().setChannel(channelFuture.channel());
                        }

                        if (channel.pipeline().get("minecraftdecoder") != null) {
                            channel.pipeline().remove("minecraftdecoder");
                        }

                        messageChannel.sendMessage(new IpSetter(channel.remoteAddress(), channelFuture.channel().localAddress()));
                        login.resetReaderIndex();
                        channelFuture.channel().writeAndFlush(login.retain());
                        channel.attr(SmartProxy.CONNECTION_STATE).set(MinecraftState.PROXY);
                    } else {
                        channel.close();
                        channelFuture.channel().close();
                    }
                });
    }

    /**
     * The last provided random service
     */
    private IGameServer lastRandom;

    /**
     * Tries to find the best free proxy which is not already full
     * depending on your search mode you provided in the config
     *
     * 'RANDOM' will just search a random proxy
     * 'BALANCED' will try to balance all proxies
     * 'FILL' will try to fill all proxies
     *
     * @param group the group you're trying to get proxies of
     * @return service if found or null
     */
    public IGameServer getFreeProxy(ITemplate group, int state) {
        if (group.getServers().size() == 1) {
            return group.getServers().get(0);
        }
        //Only free proxies
        IGameServer value = null;
        List<IGameServer> proxies = group.getServers().stream().filter(proxy -> proxy.getPlayers().size() < proxy.getMaxPlayers()).collect(Collectors.toList());
        if (!proxies.isEmpty()) {
            if (this.smartProxyConfig.getProxySearchMode().equalsIgnoreCase("RANDOM")) {
                value = proxies.get(new Random().nextInt(proxies.size()));
            } else {
                proxies.sort(Comparator.comparing(service -> service.getPlayers().size()));
                if (this.smartProxyConfig.getProxySearchMode().equalsIgnoreCase("BALANCED")) {
                    value = proxies.get(0);
                } else {
                    for (int i = proxies.size() - 1; i >= 0; i--) {
                        IGameServer server = proxies.get(i);
                        if (server.getPlayers().size() < server.getMaxPlayers()) {
                            value = server;
                            break;
                        }
                    }
                }
            }
        }
        if (value != null) {
            if (lastRandom != null) {
                if (lastRandom.getName().equalsIgnoreCase(value.getName())) {
                    return getFreeProxy(group, state);
                } else {
                    if (state == 221) {
                        lastRandom = value;
                    }
                }
            } else {
                if (state == 221) {
                    lastRandom = value;
                }
            }
        }
        if (state == 349) {
            return lastRandom;
        }
        return value;
    }
}
