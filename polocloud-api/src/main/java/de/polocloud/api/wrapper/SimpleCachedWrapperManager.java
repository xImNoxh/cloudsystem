package de.polocloud.api.wrapper;

import de.polocloud.api.wrapper.base.IWrapper;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SimpleCachedWrapperManager implements IWrapperManager {

    private List<IWrapper> wrappers;

    public SimpleCachedWrapperManager() {
        this.wrappers = new LinkedList<>();
    }

    @Override
    public void setCachedObjects(List<IWrapper> wrappers) {
        this.wrappers = wrappers;
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
    public void updateWrapper(IWrapper wrapper) {
        IWrapper safeGet = getWrapper(wrapper.getName());
        if (safeGet == null) {
            registerWrapper(wrapper);
            return;
        }
        int i = wrappers.indexOf(safeGet);

        try {
            this.wrappers.set(i, wrapper);
        } catch (Exception e) {
            //Ignoring index exception
        }
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

}
