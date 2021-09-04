package de.polocloud.api.network.packets.gameserver;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.base.SimpleGameServer;
import de.polocloud.api.network.protocol.packet.base.json.PacketSerializable;
import de.polocloud.api.network.protocol.packet.base.json.SimplePacket;
import de.polocloud.api.template.ITemplateManager;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry
public class GameServerCopyPacket extends SimplePacket {

    @PacketSerializable
    private ITemplateManager.Type copyType;

    @PacketSerializable(SimpleGameServer.class)
    private IGameServer gameServer;

    public GameServerCopyPacket() {
    }

    public GameServerCopyPacket(ITemplateManager.Type copyType, IGameServer gameServer) {
        this.copyType = copyType;
        this.gameServer = gameServer;
    }

    public IGameServer getGameServer() {
        return gameServer;
    }

    public ITemplateManager.Type getCopyType() {
        return copyType;
    }

}
