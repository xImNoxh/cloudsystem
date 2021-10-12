package de.polocloud.npcs.plugin.listeners;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.npcs.bootstraps.PluginBootstrap;
import de.polocloud.npcs.npc.base.ICloudNPC;
import de.polocloud.npcs.npc.interact.NPCInteractHandler;
import net.jitse.npclib.api.events.NPCInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * General class for handling Bukkit/Spigot events
 */
public class CollectiveSpigotListener implements Listener {

    /**
     * Handles the {@link PlayerJoinEvent} from Bukkit
     * If a player connects, the NPCs where showed/loaded for this player
     */
    @EventHandler
    public void handle(PlayerJoinEvent event){
        Bukkit.getScheduler().runTask(PluginBootstrap.getInstance(), () ->{
            for (ICloudNPC cloudNPC : PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getCloudNPCS()) {
                if(cloudNPC.getNPC() != null){
                    cloudNPC.getNPC().create();
                }
                cloudNPC.displayForPlayer(event.getPlayer());
            }
        });
    }

    /**
     * Handles the {@link PlayerQuitEvent} from Bukkit
     * If a player disconnects, the NPCs where hid/unloaded for this player
     */
    @EventHandler
    public void handle(PlayerQuitEvent event){
        Bukkit.getScheduler().runTask(PluginBootstrap.getInstance(), () ->{
            for (ICloudNPC cloudNPC : PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getCloudNPCS()) {
                cloudNPC.hideForPlayer(event.getPlayer());
            }
        });
    }

    /**
     * Handles the {@link EntityDamageEvent} from Bukkit
     * If an Entity was damaged, it checks if the Entity is a CloudEntity
     * If yes -> Prevents the damage, so the Entity should usually not die
     */
    @EventHandler
    public void handle(EntityDamageEvent event){
        List<Integer> entityIds = PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getAllEntityNPCs().stream().map(npc -> npc.getEntity().getEntityId()).collect(Collectors.toList());
        if(entityIds.contains(event.getEntity().getEntityId())){
            event.setCancelled(true);
            event.setDamage(0.0);
        }
    }

    /**
     * Handles the {@link PlayerInteractEntityEvent} from Bukkit
     * If a Player interacts at an Entity, it checks if the Entity is a CloudEntity
     * If yes -> Opens the Inventory if the CloudEntity is a Template NPC or
     * connects the player to the server if the CloudEntity is GameServer CloudEntity
     */
    @EventHandler
    public void handle(PlayerInteractEntityEvent event){
        List<Integer> entityIds = PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getAllEntityNPCs().stream().map(npc -> npc.getEntity().getEntityId()).collect(Collectors.toList());
        if(entityIds.contains(event.getRightClicked().getEntityId())){
            ICloudNPC npc = PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getNPCByEntityID(event.getRightClicked().getEntityId());
            if (npc != null) {
                event.setCancelled(true);
                if(npc.getCloudNPCMetaData().isOnlyGameServer()){
                    NPCInteractHandler.handleConnect(event.getPlayer(), npc);
                }else{
                    NPCInteractHandler.handleOpenInventory(event.getPlayer(), npc);
                }
            }
        }
    }

    /**
     * Handles the {@link EntityDeathEvent} from Bukkit
     * If a NPCEntity dies, it will be automatically respawned
     */
    @EventHandler
    public void handle(EntityDeathEvent event){
        List<Integer> entityIds = PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getAllEntityNPCs().stream().map(npc -> npc.getEntity().getEntityId()).collect(Collectors.toList());
        if(entityIds.contains(event.getEntity().getEntityId())){
            ICloudNPC npc = PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getNPCByEntityID(event.getEntity().getEntityId());
            if (npc != null) npc.update();
        }
    }

    /**
     * Handles the {@link NPCInteractEvent} from the NPCLib
     * If a Player interacts at a NPC, it checks if the Entity is a CloudNPC
     * If yes -> Opens the Inventory if the NPC is a Template NPC or
     * connects the player to the server if the NPC is GameServer NPC
     */
    @EventHandler
    public void handle(NPCInteractEvent event){
        ICloudNPC npc = PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getNPCByLocation(new Location(event.getNPC().getLocation().getWorld(), event.getNPC().getLocation().getBlockX(), event.getNPC().getLocation().getBlockY(), event.getNPC().getLocation().getBlockZ()));
        if (npc != null) {
            if(npc.getCloudNPCMetaData().isOnlyGameServer()){
                NPCInteractHandler.handleConnect(event.getWhoClicked(), npc);
            }else{
                NPCInteractHandler.handleOpenInventory(event.getWhoClicked(), npc);
            }
        }
    }

    /**
     * Handles the {@link InventoryClickEvent}
     * If a player clicks in the Server selector inventory
     * of a NPC, checks will be made and if everything
     * goes good, the player will be sent to the server
     */
    @EventHandler
    public void handle(InventoryClickEvent event){
        assert event.getWhoClicked() instanceof Player;
        Player player = (Player) event.getWhoClicked();
        if(event.getClickedInventory().getSize() == 54 && event.getView().getTitle().contains("§eSelector")){
            event.setCancelled(true);
            if(event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR) && event.getCurrentItem().hasItemMeta()){
                String gameserverName = event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[0];
                if(gameserverName != null){
                    gameserverName = ChatColor.stripColor(gameserverName);
                    IGameServer gameServer = PoloCloudAPI.getInstance().getGameServerManager().getCached(gameserverName);
                    ICloudPlayer cloudPlayer = PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(player.getName());
                    if(gameServer.getTemplate().isMaintenance()){
                        player.closeInventory();
                        player.sendMessage("§7The gameserver §8» §b" + gameServer.getName() + " §7is currently in §emaintenance§7!");
                    }else if(cloudPlayer.getMinecraftServer().getSnowflake() == gameServer.getSnowflake()){
                        player.closeInventory();
                        player.sendMessage("§7You are already on the gameserver §8» §b" + gameServer.getName() + "§7!");
                    }else if(gameServer.getOnlinePlayers() >= gameServer.getTemplate().getMaxPlayers()){
                        player.closeInventory();
                        player.sendMessage("§7The gameserver §8» §b" + gameServer.getName() + " §7is full! (§b" + gameServer.getOnlinePlayers() + "§7/§b" + gameServer.getTemplate().getMaxPlayers() + "§7)");
                    }else if(gameServer.getStatus() != GameServerStatus.AVAILABLE){
                        player.closeInventory();
                        player.sendMessage("§7The gameserver §8» §b" + gameServer.getName() + " §7is not in the AVAILABLE state!");
                    }else{
                        player.closeInventory();
                        player.sendMessage("§7Sending...");
                        cloudPlayer.sendTo(gameServer);
                        player.sendMessage("§7You were sent to §8» §b" + gameServer.getName());
                    }
                }
            }
        }
    }

}
