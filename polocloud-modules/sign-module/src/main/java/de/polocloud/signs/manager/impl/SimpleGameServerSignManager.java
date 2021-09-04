package de.polocloud.signs.manager.impl;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.signs.sign.base.IGameServerSign;
import de.polocloud.signs.manager.IGameServerSignManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.LinkedList;

public class SimpleGameServerSignManager implements IGameServerSignManager {

    private LinkedList<IGameServerSign> loadedSigns;

    public SimpleGameServerSignManager() {
        this.loadedSigns = new LinkedList<>();
    }

    @Override
    public IGameServerSign getGameSignBySign(Sign sign) {
        return loadedSigns.stream().filter(gameServerSign -> gameServerSign.getSign().equals(sign)).findAny().orElse(null);
    }

    @Override
    public IGameServerSign getFreeGameServerSign(IGameServer gameServer) {
        return loadedSigns.stream().filter(key -> key.getGameServer() == null &&
            key.getTemplate().getName().equals(gameServer.getTemplate().getName())).findFirst().orElse(null);
    }

    @Override
    public IGameServerSign getSignByGameServer(IGameServer gameServer) {
        return loadedSigns.stream().filter(key -> key.getGameServer() != null && checkSameService(key, gameServer)).findAny().orElse(null);
    }

    @Override
    public IGameServerSign getSignByLocation(Location location) {
        return loadedSigns.stream().filter(sign -> sign.getLocation().equals(location)).findFirst().orElse(null);
    }

    @Override
    public IGameServer getGameServerWithNoSign(ITemplate template) {
        return PoloCloudAPI.getInstance().getGameServerManager().getAllCached(server -> server.getTemplate().equals(template)).
            stream().filter(server -> loadedSigns.stream().anyMatch(sign -> sign.getGameServer() != null && sign.getGameServer().getName().equals(server.getName()))).findAny().orElse(null);
    }

    @Override
    public boolean checkSameService(IGameServerSign gameServerSign, IGameServer gameServer) {
        if(gameServerSign == null || gameServer == null){
            return false;
        }
        return gameServerSign.getGameServer().getSnowflake() == gameServer.getSnowflake();
    }

    @Override
    public void updateSignsGameServer(IGameServerSign sign, IGameServer gameServer) {
        if(sign == null) return;
        sign.setGameServer(gameServer);
        sign.writeSign(false);
    }

    @Override
    public void updateSignsGameServer(IGameServer gameServer) {
        IGameServerSign sign = getSignByGameServer(gameServer);
        if(sign == null) return;
        sign.setGameServer(gameServer);
        sign.writeSign(false);
    }

    @Override
    public void setSignToStopped(IGameServer gameServer) {
        IGameServerSign gameServerSign = getSignByGameServer(gameServer);
        if (gameServerSign == null) return;
        gameServerSign.setGameServer(null);
        gameServerSign.writeSign(false);
    }

    @Override
    public Block getBlockBehindSign(Block block) {
        org.bukkit.material.Sign s = (org.bukkit.material.Sign) block.getState().getData();
        return block.getRelative(s.getAttachedFace());
    }

    @Override
    public LinkedList<IGameServerSign> getLoadedSigns() {
        return this.loadedSigns;
    }
}
