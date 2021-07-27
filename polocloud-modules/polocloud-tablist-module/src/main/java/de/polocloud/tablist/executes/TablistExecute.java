package de.polocloud.tablist.executes;

import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.bootstrap.config.MasterConfig;

public interface TablistExecute {

    void execute(ICloudPlayer iCloudPlayer, MasterConfig masterConfig);

}
