package de.polocloud.bootstrap.client;

import com.google.common.collect.Lists;

import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleWrapperClientManager implements IWrapperClientManager {

    private CopyOnWriteArrayList<WrapperClient> clientList = Lists.newCopyOnWriteArrayList();


    @Override
    public CopyOnWriteArrayList<WrapperClient> getWrapperClients() {
        return this.clientList;
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
