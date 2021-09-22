package de.polocloud.api.network.packets.master;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AutoRegistry @NoArgsConstructor @Getter @AllArgsConstructor
public class MasterRequestServerStartPacket extends Packet {

    private IGameServer gameServer;

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeGameServer(gameServer);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        gameServer = buf.readGameServer();
    }

}
