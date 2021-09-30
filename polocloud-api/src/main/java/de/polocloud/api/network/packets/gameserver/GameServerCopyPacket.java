package de.polocloud.api.network.packets.gameserver;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.template.ITemplateManager;
import de.polocloud.api.common.AutoRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AutoRegistry @Getter @AllArgsConstructor @NoArgsConstructor
public class GameServerCopyPacket extends Packet {

    private ITemplateManager.Type copyType;

    private IGameServer gameServer;

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeEnum(copyType);
        buf.writeGameServer(gameServer);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        copyType = buf.readEnum();
        gameServer = buf.readGameServer();
    }
}
