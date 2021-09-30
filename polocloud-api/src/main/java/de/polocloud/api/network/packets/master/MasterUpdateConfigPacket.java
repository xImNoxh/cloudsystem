package de.polocloud.api.network.packets.master;

import de.polocloud.api.config.master.MasterConfig;
import de.polocloud.api.network.protocol.packet.base.json.PacketSerializable;
import de.polocloud.api.network.protocol.packet.base.json.SimplePacket;
import de.polocloud.api.common.AutoRegistry;

@AutoRegistry
public class MasterUpdateConfigPacket extends SimplePacket {

    @PacketSerializable
    private final MasterConfig masterConfig;

    public MasterUpdateConfigPacket(MasterConfig masterConfig) {
        this.masterConfig = masterConfig;
    }

    public MasterConfig getMasterConfig() {
        return masterConfig;
    }
}
