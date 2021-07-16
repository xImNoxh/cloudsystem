package de.polocloud.addons.signs.collectives;

import de.polocloud.addons.signs.CloudSign;
import de.polocloud.addons.signs.SignService;
import de.polocloud.addons.signs.bootstrap.Bootstrap;
import de.polocloud.addons.signs.utils.PlayerUtils;
import de.polocloud.plugin.api.spigot.event.CloudServerStartedEvent;
import de.polocloud.plugin.api.spigot.event.CloudServerStoppedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class CollectiveSignEvents implements Listener {

    public CollectiveSignEvents() {
        Bukkit.getPluginManager().registerEvents(this, Bootstrap.getInstance());
    }

    @EventHandler
    public void handle(CloudServerStoppedEvent event){
    }

    @EventHandler
    public void handle(CloudServerStartedEvent event){
        SignService.getInstance().getAddSign().execute(event.getGameServer());
    }

    @EventHandler
    public void handle(ChunkUnloadEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(PlayerInteractEvent event){
        if(event.getClickedBlock() == null) return;
        Block block = event.getClickedBlock();
        if(!block.getType().equals(Material.WALL_SIGN)) return;
        CloudSign cloudSign = SignService.getInstance().getCloudSignBySign((Sign) block.getState());

        if(cloudSign == null) return;
        PlayerUtils.sendPlayerToServer(event.getPlayer(), cloudSign.getGameServer().getName());
    }
}
