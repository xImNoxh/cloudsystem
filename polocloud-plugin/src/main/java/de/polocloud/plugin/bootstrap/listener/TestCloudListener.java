package de.polocloud.plugin.bootstrap.listener;

import de.polocloud.plugin.api.spigot.event.CloudPlayerJoinNetworkEvent;
import de.polocloud.plugin.api.spigot.event.CloudPlayerQuitNetworkEvent;
import de.polocloud.plugin.api.spigot.event.CloudPlayerSwitchServerEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.ExecutionException;

public class TestCloudListener implements Listener {

    @EventHandler
    public void handle(CloudPlayerJoinNetworkEvent event) {
        event.getPlayer().thenAccept(player -> {
            System.out.println("Player " + player.getName() + " joined the network");
        });


    }

    @EventHandler
    public void handle(CloudPlayerQuitNetworkEvent event) {
        System.out.println("Player quit");
        event.getPlayer().thenAccept(player -> {
            System.out.println("Player " + player.getName() + " quit the network");
        });

    }

    @EventHandler
    public void handle(CloudPlayerSwitchServerEvent event) {
        System.out.println("Player switched " + event.getPlayerName());
        event.getPlayer().thenAccept(player -> {
            try {
                System.out.println("Player " + player.getName() + " switched from " + event.getFrom().get().getName() + " to " + event.getTo().get().getName());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }


}
