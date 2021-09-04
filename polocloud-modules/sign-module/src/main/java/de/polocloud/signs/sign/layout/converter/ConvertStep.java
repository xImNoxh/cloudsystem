package de.polocloud.signs.sign.layout.converter;

import de.polocloud.signs.sign.base.IGameServerSign;

public interface ConvertStep {

    Object convert(IGameServerSign gameServerSign);

}
