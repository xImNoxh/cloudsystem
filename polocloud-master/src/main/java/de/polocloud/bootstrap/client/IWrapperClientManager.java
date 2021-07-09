package de.polocloud.bootstrap.client;

import java.util.Collection;

public interface IWrapperClientManager {

    Collection<WrapperClient> getWrapperClients();

    void registerWrapperClient(WrapperClient wrapperClient);

}
