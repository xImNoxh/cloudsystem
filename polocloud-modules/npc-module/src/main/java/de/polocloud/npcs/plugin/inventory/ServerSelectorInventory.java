package de.polocloud.npcs.plugin.inventory;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.template.base.ITemplate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ServerSelectorInventory {

    private ITemplate template;
    private Player player;

    private Inventory inventory;

    public ServerSelectorInventory(ITemplate template, Player player) {
        this.template = template;
        this.player = player;
        if(template != null){
            initInventory();
        }
    }

    /**
     * Initializes the Selector-Inventory
     */
    public void initInventory(){
        this.inventory = Bukkit.createInventory(null, 54, "§eSelector §8(§b" + template.getName() + "§8)");
        if(template.getServers().isEmpty()){
            ItemStack noServers = new ItemStack(Material.BARRIER);
            ItemMeta noServersMeta = noServers.getItemMeta();
            noServersMeta.setDisplayName("§cNo §7servers in §b" + template.getName() + " §7are online!");
            noServers.setItemMeta(noServersMeta);
            inventory.setItem(22, noServers);
        }else{
            ItemStack full = new ItemStack(Material.STAINED_CLAY, 1, (byte)4);
            ItemStack online = new ItemStack(Material.STAINED_CLAY, 1, (byte)5);
            ItemStack players = new ItemStack(Material.STAINED_CLAY, 1, (byte)13);
            ItemStack maintenance = new ItemStack(Material.HARD_CLAY, 1, (byte)0);

            int start = 9;
            for (IGameServer gameServer : template.getServers().stream().filter(server -> server.getStatus().equals(GameServerStatus.AVAILABLE)).collect(Collectors.toList())) {
                int amount = Math.min(gameServer.getOnlinePlayers(), 64);
                if(start >= 44){
                    break;
                }
                if(template.isMaintenance()){
                    ItemStack maintenanceClone = maintenance.clone();
                    maintenanceClone.setAmount(amount);
                    ItemMeta meta = maintenanceClone.getItemMeta();
                    meta.setDisplayName("§b" + gameServer.getName() + " §7(§b" + template.getName() + "§7)");
                    meta.setLore(Arrays.asList("", "§7This server is in §emaintenance§7!", "§b" + gameServer.getOnlinePlayers() + "§7/§b" + template.getMaxPlayers(), ""));
                    maintenanceClone.setItemMeta(meta);
                    inventory.setItem(start, maintenanceClone);
                } else if(gameServer.getOnlinePlayers() >= template.getMaxPlayers()){
                    ItemStack fullClone = full.clone();
                    fullClone.setAmount(amount);
                    ItemMeta meta = fullClone.getItemMeta();
                    meta.setDisplayName("§b" + gameServer.getName() + " §7(§b" + template.getName() + "§7)");
                    meta.setLore(Arrays.asList("", "§7This server is §cfull§7!", "§b" + gameServer.getOnlinePlayers() + "§7/§b" + template.getMaxPlayers(), ""));
                    fullClone.setItemMeta(meta);
                    inventory.setItem(start, fullClone);
                } else if(gameServer.getOnlinePlayers() > 0){
                    ItemStack playersClone = players.clone();
                    playersClone.setAmount(amount);
                    ItemMeta meta = playersClone.getItemMeta();
                    meta.setDisplayName("§b" + gameServer.getName() + " §7(§b" + template.getName() + "§7)");
                    meta.setLore(Arrays.asList("", "§7This server is §aonline§7!", "§b" + gameServer.getOnlinePlayers() + "§7/§b" + template.getMaxPlayers(), "§7Click to connect!", ""));
                    playersClone.setItemMeta(meta);
                    inventory.setItem(start, playersClone);
                } else {
                    ItemStack onlineClone = online.clone();
                    onlineClone.setAmount(amount);
                    ItemMeta meta = onlineClone.getItemMeta();
                    meta.setDisplayName("§b" + gameServer.getName() + " §7(§b" + template.getName() + "§7)");
                    meta.setLore(Arrays.asList("", "§7This server is §aonline§7!", "§b" + gameServer.getOnlinePlayers() + "§7/§b" + template.getMaxPlayers(), "§7Click to connect!", ""));
                    onlineClone.setItemMeta(meta);
                    inventory.setItem(start, onlineClone);
                }
                start++;
            }
        }
    }

    /**
     * Method for opening the Selector inventory
     */
    public void open(){
        if (inventory != null) {
            player.openInventory(inventory);
        }
    }
}
