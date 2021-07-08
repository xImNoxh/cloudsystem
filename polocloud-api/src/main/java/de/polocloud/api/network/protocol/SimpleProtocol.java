package de.polocloud.api.network.protocol;

import com.esotericsoftware.kryonetty.kryo.KryoNetty;
import com.google.inject.Inject;
import de.polocloud.api.network.protocol.packet.StartServerPacket;
import de.polocloud.api.network.protocol.packet.TestPacket;

import javax.inject.Named;

public class SimpleProtocol implements IProtocol {

    private KryoNetty protocol;

    @Inject
    @Named(value = "setting_protocol_threadSize")
    private int threadSize = 8;

    @Override
    public KryoNetty getProtocol() {
        if (this.protocol == null) {
            this.protocol = create();
            registerPackets(this.protocol);
        }
        return this.protocol;
    }

    private void registerPackets(KryoNetty kryoNetty) {
        kryoNetty.register(100, TestPacket.class);
        kryoNetty.register(101, StartServerPacket.class);
    }

    @Override
    public KryoNetty create() {
        return new KryoNetty()
            .useExecution()
            .threadSize(this.threadSize);
    }
}
