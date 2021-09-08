package de.polocloud.signs.sign.layout.converter;

import de.polocloud.signs.sign.base.IGameServerSign;

public interface ConvertStep {

    /**
     *  Converts a String with a placeholder correctly
     *  for the {@link IGameServerSign}
     * @param gameServerSign sign for converting
     * @return the converted {@link Object}
     */
    Object convert(IGameServerSign gameServerSign);

}
