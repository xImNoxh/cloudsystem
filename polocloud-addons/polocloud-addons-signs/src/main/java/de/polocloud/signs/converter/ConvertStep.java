package de.polocloud.signs.converter;

import de.polocloud.signs.signs.IGameServerSign;

public interface ConvertStep {

    Object execute(IGameServerSign gameServerSign);

}
