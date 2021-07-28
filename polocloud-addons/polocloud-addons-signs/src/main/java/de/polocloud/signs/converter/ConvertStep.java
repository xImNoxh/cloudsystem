package de.polocloud.signs.converter;

import de.polocloud.api.gameserver.IGameServer;

public interface ConvertStep {

    Object execute(IGameServer gameServer);

}
