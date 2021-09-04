package de.polocloud.api.network.packets.api;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.fallback.base.IFallback;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.port.IPortManager;
import de.polocloud.api.gameserver.port.SimpleCachedPortManager;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.util.PoloHelper;
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
    private SimpleCachedPortManager portManager;

    public GlobalCachePacket() {
        MasterCache masterCache = new MasterCache(PoloCloudAPI.getInstance());

        this.gameServers = masterCache.getGameServers();
        this.cloudPlayers = masterCache.getCloudPlayers();
        this.templates = masterCache.getTemplates();
        this.wrappers = masterCache.getWrappers();
        this.fallbacks = masterCache.getFallbacks();
        this.portManager = (SimpleCachedPortManager) PoloCloudAPI.getInstance().getPortManager();

        this.clearDoubles();
    }


    public void clearDoubles() {

        List<IGameServer> checkedGameServers = new ArrayList<>();
        List<ICloudPlayer> checkedCloudPlayers = new ArrayList<>();
        List<IWrapper> checkedWrappers = new ArrayList<>();
        List<ITemplate> checkedTemplates = new ArrayList<>();

        for (IGameServer gameServer : this.gameServers) {
            checkedGameServers.removeIf(gs -> gs.getName().equalsIgnoreCase(gameServer.getName()));
            checkedGameServers.add(gameServer);
        }

        for (ICloudPlayer cloudPlayer : this.cloudPlayers) {
            checkedCloudPlayers.removeIf(cp -> cp.getName().equalsIgnoreCase(cloudPlayer.getName()));
            checkedCloudPlayers.add(cloudPlayer);
        }

        for (IWrapper wrapper : wrappers) {
            checkedWrappers.removeIf(w -> w.getName().equalsIgnoreCase(wrapper.getName()));
            checkedWrappers.add(wrapper);
        }

        for (ITemplate template : templates) {
            checkedTemplates.removeIf(tp -> tp.getName().equalsIgnoreCase(template.getName()));
            checkedTemplates.add(template);
        }

        this.gameServers = checkedGameServers;
        this.cloudPlayers = checkedCloudPlayers;
        this.templates = checkedTemplates;
        this.wrappers = checkedWrappers;
    }

    public MasterCache getMasterCache() {
        return new MasterCache(this.gameServers, this.cloudPlayers, this.templates, this.wrappers, this.fallbacks);
    }

    public IPortManager getPortManager() {
        return portManager;
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

        buf.writeString(PoloHelper.GSON_INSTANCE.toJson(this.portManager));
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

        String json = buf.readString();
        this.portManager = PoloHelper.GSON_INSTANCE.fromJson(json, SimpleCachedPortManager.class);
    }
}
