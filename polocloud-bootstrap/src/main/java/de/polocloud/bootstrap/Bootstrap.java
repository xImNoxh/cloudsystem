package de.polocloud.bootstrap;

import de.polocloud.logger.log.Logger;

public class Bootstrap {

    /**
     * Starts a new {@link Logger} instance and creates a new Cloud instance
     * @param args the program arguments
     */
    public static void main(String[] args) {
        Logger logger = new Logger();
        logger.boot();
        new CloudInit(args);
    }

}
