package de.polocloud.bootstrap.client;

import com.google.common.collect.Lists;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleWrapperClientManager implements IWrapperClientManager {

    private CopyOnWriteArrayList<WrapperClient> clientList = Lists.newCopyOnWriteArrayList();


    @Override
    public CopyOnWriteArrayList<WrapperClient> getWrapperClients() {
        return this.clientList;
    }

    public WrapperClient getWrapperClientByName(String wrapperName) {
        return clientList.stream().filter(wrapperClient -> wrapperClient.getName().equalsIgnoreCase(wrapperName)).findFirst().orElse(null);
    }

    @Override
    public WrapperClient getWrapperClientByConnection(ChannelHandlerContext channelHandlerContext) {
        for (WrapperClient wrapperClient : clientList) {
            if (wrapperClient.getConnection().channel().id().asLongText().equals(channelHandlerContext.channel().id().asLongText())) {
                return wrapperClient;
            }
        }
        return null;
    }

    @Override
    public void registerWrapperClient(WrapperClient wrapperClient) {
        clientList.add(wrapperClient);
    }

    @Override
    public void removeWrapper(WrapperClient wrapperClient) {
        clientList.remove(wrapperClient);
    }

}
