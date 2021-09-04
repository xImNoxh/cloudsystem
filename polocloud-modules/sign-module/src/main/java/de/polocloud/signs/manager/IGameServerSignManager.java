package de.polocloud.signs.manager;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.signs.sign.base.IGameServerSign;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.LinkedList;

public interface IGameServerSignManager {

    IGameServerSign getGameSignBySign(Sign sign);

    IGameServerSign getFreeGameServerSign(IGameServer gameServer);

    IGameServerSign getSignByGameServer(IGameServer gameServer);

    IGameServerSign getSignByLocation(Location location);

    IGameServer getGameServerWithNoSign(ITemplate template);

    Block getBlockBehindSign(Block block);

    LinkedList<IGameServerSign> getLoadedSigns();

    boolean checkSameService(IGameServerSign gameServerSign, IGameServer gameServer);

    void updateSignsGameServer(IGameServerSign sign, IGameServer gameServer);

    void updateSignsGameServer(IGameServer gameServer);

    void setSignToStopped(IGameServer gameServer);

}
