package de.polocloud.bootstrap;

import de.polocloud.bootstrap.configuration.setup.executes.ServiceTypeSetup;
import de.polocloud.logger.log.Logger;

public class Bootstrap {

    public static void main(String[] args) {
        Logger.boot();
        new CloudInit(args);
    }
}
