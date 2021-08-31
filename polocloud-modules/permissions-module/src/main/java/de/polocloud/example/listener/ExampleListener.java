package de.polocloud.example.listener;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.player.CloudPlayerJoinNetworkEvent;
import de.polocloud.api.player.ICloudPlayer;

public class ExampleListener implements IListener {

    @EventHandler
    public void handle(CloudPlayerJoinNetworkEvent event) {
        ICloudPlayer player = event.getPlayer();
        System.out.println(player.getName() + " joined the network");
        player.sendMessage("Hallo Du hs!");
    }
}
