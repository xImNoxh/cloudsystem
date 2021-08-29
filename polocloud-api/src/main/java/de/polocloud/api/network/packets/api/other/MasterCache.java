package de.polocloud.api.network.packets.api.other;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.fallback.base.IFallback;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.protocol.IProtocolObject;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.wrapper.base.IWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MasterCache implements IProtocolObject {

    private List<IGameServer> gameServers;
    private List<ICloudPlayer> cloudPlayers;
    private List<ITemplate> templates;
    private List<IWrapper> wrappers;
    private List<IFallback> fallbacks;

    public MasterCache() {
    }

    public MasterCache(PoloCloudAPI cloudAPI) {
        this(
            cloudAPI.getGameServerManager() == null ? new LinkedList<>() : cloudAPI.getGameServerManager().getAllCached(),
            cloudAPI.getCloudPlayerManager() == null ? new LinkedList<>() : cloudAPI.getCloudPlayerManager().getAllCached(),
            cloudAPI.getTemplateManager() == null ? new LinkedList<>() : cloudAPI.getTemplateManager().getTemplates(),
            cloudAPI.getWrapperManager() == null ? new LinkedList<>() : cloudAPI.getWrapperManager().getWrappers(),
            cloudAPI.getFallbackManager() == null ? new LinkedList<>() : cloudAPI.getFallbackManager().getAvailableFallbacks()
        );
    }

    public MasterCache(List<IGameServer> gameServers, List<ICloudPlayer> cloudPlayers, List<ITemplate> templates, List<IWrapper> wrappers, List<IFallback> fallbacks) {
        this.gameServers = gameServers;
        this.cloudPlayers = cloudPlayers;
        this.templates = templates;
        this.wrappers = wrappers;
        this.fallbacks = fallbacks;
    }

    //To avoid concurrentmodificationexception
    public List<IWrapper> getWrappers() {
        return new LinkedList<>(wrappers);
    }

    //To avoid concurrentmodificationexception
    public List<IFallback> getFallbacks() {
        return new LinkedList<>(fallbacks);
    }

    //To avoid concurrentmodificationexception
    public List<IGameServer> getGameServers() {
        return new LinkedList<>(gameServers);
    }

    //To avoid concurrentmodificationexception
    public List<ICloudPlayer> getCloudPlayers() {
        return new LinkedList<>(cloudPlayers);
    }

    //To avoid concurrentmodificationexception
    public List<ITemplate> getTemplates() {
        return new LinkedList<>(templates);
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {

        this.gameServers = new LinkedList<>(this.gameServers);
        this.cloudPlayers = new LinkedList<>(this.cloudPlayers);
        this.templates = new LinkedList<>(this.templates);
        this.wrappers = new LinkedList<>(this.wrappers);


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

        size = buf.readInt();
        wrappers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            wrappers.add(buf.readWrapper());
        }

    }
}
