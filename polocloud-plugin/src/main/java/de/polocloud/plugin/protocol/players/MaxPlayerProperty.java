package de.polocloud.plugin.protocol.players;

import de.polocloud.plugin.function.BootstrapFunction;

public class MaxPlayerProperty {

    private int maxPlayers;
    private BootstrapFunction bootstrapFunction;
    private String message;


    public MaxPlayerProperty(BootstrapFunction bootstrapFunction) {
        this.bootstrapFunction = bootstrapFunction;
        maxPlayers = bootstrapFunction.getMaxPlayers();
    }

    public BootstrapFunction getBootstrapFunction() {
        return bootstrapFunction;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
