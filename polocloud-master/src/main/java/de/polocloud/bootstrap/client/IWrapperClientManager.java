package de.polocloud.bootstrap.client;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.CopyOnWriteArrayList;

public interface IWrapperClientManager {

    CopyOnWriteArrayList<WrapperClient> getWrapperClients();

    WrapperClient getWrapperClientByName(String wrapperName);

    WrapperClient getWrapperClientByConnection(ChannelHandlerContext channelHandlerContext);

    void registerWrapperClient(WrapperClient wrapperClient);

    void removeWrapper(WrapperClient wrapperClient);
}
