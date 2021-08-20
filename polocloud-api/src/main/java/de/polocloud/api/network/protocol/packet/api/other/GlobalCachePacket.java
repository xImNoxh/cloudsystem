package de.polocloud.api.network.protocol.packet.api.other;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.ITemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GlobalCachePacket extends Packet {


    private List<IGameServer> gameServers;
    private List<ICloudPlayer> cloudPlayers;
    private List<ITemplate> templates;

    public GlobalCachePacket() {
        try {
            this.gameServers = PoloCloudAPI.getInstance().getGameServerManager().getGameServers().get();
            this.cloudPlayers = PoloCloudAPI.getInstance().getCloudPlayerManager().getAllOnlinePlayers().get();
            this.templates = new LinkedList<>(PoloCloudAPI.getInstance().getTemplateService().getLoadedTemplates().get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {

        buf.writeInt(gameServers.size());
        for (IGameServer gameServer : gameServers) {
            buf.writeGameServer(gameServer);
        }

        buf.writeInt(cloudPlayers.size());
        for (ICloudPlayer cloudPlayer : cloudPlayers) {
            buf.writeCloudPlayer(cloudPlayer);
        }

        buf.writeInt(templates.size());
        for (ITemplate template : templates) {
            buf.writeTemplate(template);
        }
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {

        int size = buf.readInt();
        gameServers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            gameServers.add(buf.readGameServer());
        }

        size = buf.readInt();
        cloudPlayers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            cloudPlayers.add(buf.readCloudPlayer());
        }

        size = buf.readInt();
        templates = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            templates.add(buf.readTemplate());
        }
    }
}
