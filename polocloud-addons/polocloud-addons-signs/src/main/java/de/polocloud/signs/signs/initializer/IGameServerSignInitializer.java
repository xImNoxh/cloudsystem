package de.polocloud.signs.signs.initializer;

import de.polocloud.signs.SignService;
import de.polocloud.signs.signs.ConfigSignLocation;
import de.polocloud.signs.signs.IGameServerSign;

public class IGameServerSignInitializer {

    public IGameServerSignInitializer() {

    }

    public void addSign(ConfigSignLocation configSignLocation){
        SignService.getInstance().getCache().add(new IGameServerSign(configSignLocation));
    }

}
