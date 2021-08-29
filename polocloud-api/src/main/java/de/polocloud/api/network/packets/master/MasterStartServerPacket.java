package de.polocloud.api.network.packets.master;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.wrapper.base.IWrapper;

import java.io.IOException;

@AutoRegistry//(id = 0x38)
public class MasterStartServerPacket extends Packet {

    private IGameServer gameServer;
    private IWrapper wrapper;

    public MasterStartServerPacket() {
    }

    public MasterStartServerPacket(IGameServer gameServer, IWrapper wrapper) {
        this.gameServer = gameServer;
        this.wrapper = wrapper;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeGameServer(gameServer);
        buf.writeWrapper(wrapper);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        this.gameServer = buf.readGameServer();
        this.wrapper = buf.readWrapper();
    }

    public IGameServer getGameServer() {
        return gameServer;
    }

    public IWrapper getWrapper() {
        return wrapper;
    }
}
