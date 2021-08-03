package de.polocloud.bootstrap.client;

import java.util.concurrent.CopyOnWriteArrayList;

public interface IWrapperClientManager {

    CopyOnWriteArrayList<WrapperClient> getWrapperClients();

    WrapperClient getWrapperClientByName(String wrapperName);

    void registerWrapperClient(WrapperClient wrapperClient);

    void removeWrapper(WrapperClient wrapperClient);
}
