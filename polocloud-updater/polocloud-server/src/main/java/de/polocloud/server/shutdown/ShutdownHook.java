package de.polocloud.server.shutdown;

import de.polocloud.server.PoloCloudServer;

public class ShutdownHook {

    public void registerHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (PoloCloudServer.getInstance() != null)
                PoloCloudServer.getInstance().shutdown();
        }));
    }

}
