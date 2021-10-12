package de.polocloud.api.messaging.def;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.messaging.IMessageManager;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.api.messaging.IMessageListener;
import de.polocloud.api.network.packets.other.ChannelMessagePacket;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.util.map.MapEntry;
import de.polocloud.api.util.map.UniqueMap;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class SimpleCachedMessageManager implements IMessageManager {

    /**
     * ALl registered channels
     */
    private final UniqueMap<Class<?>, IMessageChannel<?>> registeredChannels;

    public SimpleCachedMessageManager() {
        this.registeredChannels = new UniqueMap<>();

        Scheduler.runtimeScheduler().schedule(() -> PoloCloudAPI.getInstance().getConnection().getProtocol().registerPacketHandler(new IPacketHandler<ChannelMessagePacket>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, ChannelMessagePacket packet) {
                IMessageChannel<Object> messageChannel = getChannel(packet.getChannel());

                if (messageChannel != null) {
                    for (IMessageListener listener : ((SimpleMessageChannel<?>) messageChannel).getListeners()) {
                        try {
                            listener.handleMessage(packet.getWrappedObject(), packet.getStartTime());
                        } catch (Exception e) {
                            PoloCloudAPI.getInstance().reportException(e);
                            PoloLogger.print(LogLevel.ERROR, "An exception was caught while handling a message!");
                        }
                    }
                }

            }

            @Override
            public Class<ChannelMessagePacket> getPacketClass() {
                return ChannelMessagePacket.class;
            }
        }), () -> PoloCloudAPI.getInstance().getConnection() != null);
    }

    @Override
    public void unregisterChannel(IMessageChannel<?> messageChannel) {
        for (MapEntry<Class<?>, IMessageChannel<?>> entry : this.registeredChannels.iterable()) {
            IMessageChannel<?> value = entry.getValue();
            Class<?> key = entry.getKey();
            if (value.getSnowflake() == messageChannel.getSnowflake()) {
                this.registeredChannels.remove().atKey(key);
            }
        }
    }

    @Override
    public <T> IMessageChannel<T> registerChannel(Class<T> wrapperClass, String channelName) {
        IMessageChannel<T> messageChannel = new SimpleMessageChannel<>(channelName);

        this.registeredChannels.put(wrapperClass).toValue(messageChannel);
        return messageChannel;
    }


    @Override
    public <T> IMessageChannel<T> getChannel(String channelName) {
        return (IMessageChannel<T>) this.registeredChannels.get().allValues().stream().filter(messageChannel -> messageChannel.getName().equalsIgnoreCase(channelName)).findFirst().orElse(null);
    }

    @Override
    public List<IMessageChannel<?>> getChannels() {
        return this.registeredChannels.get().allValues();
    }

}
