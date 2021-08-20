package de.polocloud.bootstrap;

import de.polocloud.logger.log.Logger;

public class Bootstrap {

    public static void main(String[] args) {
        Logger.boot();
        new CloudInit(args);
    }

}
