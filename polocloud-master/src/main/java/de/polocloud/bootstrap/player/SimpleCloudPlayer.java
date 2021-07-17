package de.polocloud.bootstrap.player;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.master.MasterPlayerKickPacket;
import de.polocloud.api.player.ICloudPlayer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.UUID;

public class SimpleCloudPlayer implements ICloudPlayer {

    private String name;
    private UUID uuid;

    private IGameServer proxyGameServer;
    private IGameServer minecraftGameServer;

    public SimpleCloudPlayer(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }


    @Override

    public String getName() {
        return this.name;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public IGameServer getProxyServer() {
        return this.proxyGameServer;
    }

    @Override
    public IGameServer getMinecraftServer() {
        return this.minecraftGameServer;
    }

    public void setMinecraftGameServer(IGameServer minecraftGameServer) {
        this.minecraftGameServer = minecraftGameServer;
    }

    public void setProxyGameServer(IGameServer proxyGameServer) {
        this.proxyGameServer = proxyGameServer;
    }

    @Override
    public void sendMessage(String message) {
        throw new NotImplementedException();
    }

    @Override
    public void sendTo(IGameServer gameServer) {
        throw new NotImplementedException();
    }

    @Override
    public void kick(String message) {
        getProxyServer().sendPacket(new MasterPlayerKickPacket(getUUID(), message));
    }
}
