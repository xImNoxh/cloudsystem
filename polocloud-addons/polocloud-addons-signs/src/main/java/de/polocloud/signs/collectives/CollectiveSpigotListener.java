package de.polocloud.signs.collectives;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.plugin.api.spigot.event.CloudServerStartedEvent;
import de.polocloud.plugin.api.spigot.event.CloudServerStoppedEvent;
import de.polocloud.plugin.api.spigot.event.CloudServerUpdatedEvent;
import de.polocloud.signs.SignService;
import de.polocloud.signs.bootstrap.SignBootstrap;
import de.polocloud.signs.signs.IGameServerSign;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CollectiveSpigotListener implements Listener {

    public CollectiveSpigotListener() {
        Bukkit.getPluginManager().registerEvents(this, SignBootstrap.getInstance());
    }

    @EventHandler
    public void handle(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || !event.getClickedBlock().getType().equals(Material.WALL_SIGN)) return;
        Sign sign = (Sign) event.getClickedBlock().getState();
        IGameServerSign gameSign = SignService.getInstance().getCache().stream().filter(s -> s.getSign().equals(sign)).findAny().get();
        if (gameSign == null || gameSign.getGameServer() == null) return;
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(gameSign.getGameServer().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        event.getPlayer().sendPluginMessage(SignBootstrap.getInstance(), "BungeeCord", b.toByteArray());
    }

    @EventHandler
    public void handle(SignChangeEvent event) {
        Sign sign = (Sign) event.getBlock().getState();
        IGameServerSign gameSign = SignService.getInstance().getCache().stream().filter(s -> s.getLocation().equals(sign.getLocation())).findAny().get();
        if (gameSign == null) return;
        gameSign.reloadSign(sign);
        gameSign.updateSign();
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(CloudServerStartedEvent event){
        IGameServerSign sign = SignService.getInstance().getFreeTemplateSign(event.getGameServer());
        sign.setGameServer(event.getGameServer());
        sign.displayService();
    }

    @EventHandler
    public void handle(CloudServerUpdatedEvent event){



    }

    @EventHandler
    public void handle(CloudServerStoppedEvent event){

        IGameServer gameServer = event.getGameServer();

        IGameServerSign gameServerSign = SignService.getInstance().getCache().stream().filter(key -> key.getGameServer().getSnowflake() == gameServer.getSnowflake()).findAny().get();
        if(gameServerSign == null) return;

        gameServerSign.setGameServer(null);
        gameServerSign.writeSign("none");

    }

}
