package de.polocloud.api.network;

import com.esotericsoftware.kryonetty.network.handler.NetworkListener;

public interface INetworkable {

    void registerListener(NetworkListener networkListener);

}
