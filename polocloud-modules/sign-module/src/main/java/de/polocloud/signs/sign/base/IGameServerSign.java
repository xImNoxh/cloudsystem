package de.polocloud.signs.sign.base;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.signs.sign.enumeration.SignState;
import de.polocloud.signs.sign.location.SignLocation;
import org.bukkit.Location;
import org.bukkit.block.Sign;

public interface IGameServerSign {

    void writeSign(boolean writeClean);

    void cleanUp();

    void updateSignState();

    void updateSign();

    void reloadSign(Sign sign);

    IGameServer getGameServer();

    ITemplate getTemplate();

    SignState getSignState();

    Sign getSign();

    SignLocation getSignLocation();

    Location getLocation();

    void setGameServer(IGameServer gameServer);

    void setTemplate(ITemplate template);

    void setSignLocation(SignLocation location);

    void setSignState(SignState signState);

    void setSign(Sign sign);

    void setLocation(Location location);

}
