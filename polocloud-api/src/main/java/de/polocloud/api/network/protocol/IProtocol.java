package de.polocloud.api.network.protocol;

import com.esotericsoftware.kryonetty.kryo.KryoNetty;

public interface IProtocol {

    KryoNetty getProtocol();

    KryoNetty create();

}
