package de.polocloud.api.wrapper;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperCachePacket;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SimpleCachedWrapperManager implements IWrapperManager {

    private final List<IWrapper> wrappers;

    public SimpleCachedWrapperManager() {
        this.wrappers = new LinkedList<>();
    }


    @Override
    public List<IWrapper> getWrappers() {
        return Collections.synchronizedList(wrappers);
    }

    @Override
    public IWrapper getWrapper(String name) {
        return this.wrappers.stream().filter(wrapper -> wrapper.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public IWrapper getWrapper(ChannelHandlerContext channelHandlerContext) {
        for (IWrapper wrapper : wrappers) {
            if (wrapper.ctx().channel().id().asLongText().equals(channelHandlerContext.channel().id().asLongText())) {
                return wrapper;
            }
        }
        return null;
    }

    @Override
    public void registerWrapper(IWrapper wrapper) {
        if (!this.wrappers.contains(wrapper)) {
            this.wrappers.add(wrapper);
        }
    }

    @Override
    public void unregisterWrapper(IWrapper wrapper) {
        IWrapper safeGet = this.getWrapper(wrapper.getName());
        if (safeGet != null) {
            this.wrappers.remove(safeGet);
        }
    }

    @Override
    public void syncCache() {
        WrapperCachePacket packet = new WrapperCachePacket();
        PoloCloudAPI.getInstance().getConnection().sendPacket(packet, PoloType.PLUGIN_PROXY);
        PoloCloudAPI.getInstance().getConnection().sendPacket(packet, PoloType.PLUGIN_SPIGOT);
    }

    @Override
    public void syncCache(IGameServer gameServer) {
        gameServer.sendPacket(new WrapperCachePacket());
    }
}
