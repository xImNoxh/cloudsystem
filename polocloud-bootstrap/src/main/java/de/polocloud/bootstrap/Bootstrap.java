package de.polocloud.bootstrap;

import de.polocloud.api.logger.PoloLogger;
import de.polocloud.logger.log.Logger;

public class Bootstrap {

    public static void main(String[] args) {
        Logger logger = new Logger();
        logger.boot();
        new CloudInit(args);
    }

}
