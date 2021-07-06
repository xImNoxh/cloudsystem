package de.polocloud.master.service.hook;

public class ShutdownHook {

    public ShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //TODO
        }));
    }

}
