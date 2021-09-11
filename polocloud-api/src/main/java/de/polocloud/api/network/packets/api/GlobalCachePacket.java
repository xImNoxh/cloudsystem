package de.polocloud.api.network.packets.api;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.master.MasterConfig;
import de.polocloud.api.fallback.base.IFallback;
import de.polocloud.api.gameserver.base.IGameServer;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.wrapper.base.IWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AutoRegistry
public class GlobalCachePacket extends Packet {

    private List<IGameServer> gameServers;
    private List<ICloudPlayer> cloudPlayers;
    private List<ITemplate> templates;
    private List<IWrapper> wrappers;
    private List<IFallback> fallbacks;
    private MasterConfig masterConfig;

    public GlobalCachePacket() {
        MasterCache masterCache = new MasterCache(PoloCloudAPI.getInstance());

        this.gameServers = masterCache.getGameServers();
        this.cloudPlayers = masterCache.getCloudPlayers();
        this.templates = masterCache.getTemplates();
        this.wrappers = masterCache.getWrappers();
        this.fallbacks = masterCache.getFallbacks();
        this.masterConfig = PoloCloudAPI.getInstance().getMasterConfig();
    }

    public MasterCache getMasterCache() {
        return new MasterCache(this.gameServers, this.cloudPlayers, this.templates, this.wrappers, this.fallbacks, masterConfig);
    }

    public MasterConfig getMasterConfig() {
        return masterConfig;
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

        buf.writeInt(wrappers.size());
        for (IWrapper wrapper : wrappers) {
            buf.writeWrapper(wrapper);
        }

        buf.writeInt(fallbacks.size());
        for (IFallback fallback : fallbacks) {
            buf.writeFallback(fallback);
        }

        buf.writeProtocol(this.masterConfig);

       // buf.writeString(PoloHelper.GSON_INSTANCE.toJson(this.masterConfig));
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {

        int size = buf.readInt();

        this.gameServers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.gameServers.add(buf.readGameServer());
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

        size = buf.readInt();
        wrappers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            wrappers.add(buf.readWrapper());
        }

        size = buf.readInt();
        fallbacks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            fallbacks.add(buf.readFallback());
        }

        this.masterConfig = buf.readProtocol();
        //this.masterConfig = PoloHelper.GSON_INSTANCE.fromJson(buf.readString(), MasterConfig.class);
    }
}
