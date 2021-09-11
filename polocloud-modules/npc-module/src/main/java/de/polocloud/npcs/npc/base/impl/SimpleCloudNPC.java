package de.polocloud.npcs.npc.base.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.npcs.bootstraps.PluginBootstrap;
import de.polocloud.npcs.npc.base.ICloudNPC;
import de.polocloud.npcs.npc.base.meta.CloudNPCMeta;
import de.polocloud.npcs.npc.skin.GameProfileFetcher;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.skin.Skin;
import net.jitse.npclib.api.state.NPCSlot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;

public class SimpleCloudNPC implements ICloudNPC {

    private CloudNPCMeta meta;

    private Location location;

    private ItemStack itemInHand;

    private NPC npc;

    public SimpleCloudNPC(CloudNPCMeta metaData) {
        this.meta = metaData;

        this.location = new Location(Bukkit.getWorld(metaData.getWorld()), metaData.getX(), metaData.getY(), metaData.getZ(), metaData.getYaw(), metaData.getPitch());


        if(!metaData.getItemInHand().equalsIgnoreCase("none")){
            itemInHand = new ItemStack(Material.getMaterial(metaData.getItemInHand().toUpperCase()));
        }else{
            itemInHand = null;
        }
    }

    @Override
    public CloudNPCMeta getCloudNPCMetaData() {
        return this.meta;
    }

    @Override
    public void setCloudNPCMetaData(CloudNPCMeta metaData) {
        this.meta = metaData;
    }

    @Override
    public void spawn() {
        String skinName;
        if(meta.getSkinName().equalsIgnoreCase("default")){
            skinName = "HttpMarco";
        }else{
            skinName = meta.getSkinName();
        }


        Bukkit.getScheduler().runTask(PluginBootstrap.getInstance(), () ->{
            if(!this.location.getChunk().isLoaded()){
                this.location.getChunk().load();
            }
        });

        this.npc = PluginBootstrap.getInstance().getNpcService().getNpcLib().createNPC();
        npc.setLocation(location.add(0.5, 0, 0.5));
        npc.setItem(NPCSlot.MAINHAND, itemInHand == null ? new ItemStack(Material.AIR) : itemInHand);


        ArrayList<String> hologramLines = new ArrayList<>();
        hologramLines.add("§e" + this.meta.getTemplateOrGameServer());
        if(this.meta.isOnlyGameServer()){
            IGameServer gameServer = PoloCloudAPI.getInstance().getGameServerManager().getCached(this.meta.getTemplateOrGameServer());
            if (gameServer == null) {
                hologramLines.add("§cOffline");
            }else{
                hologramLines.add("§b" + gameServer.getOnlinePlayers() + "§7/§b" + gameServer.getTemplate().getMaxPlayers());
            }
        }else{
            int serversOnline = PoloCloudAPI.getInstance().getGameServerManager().getAllCached(PoloCloudAPI.getInstance().getTemplateManager().getTemplate(this.meta.getTemplateOrGameServer())).size();
            hologramLines.add("§b" + serversOnline + (serversOnline == 1 ? " §7Server" : " §7Servers"));
        }
        npc.setText(hologramLines);

        GameProfile gameProfile;
        try {
            gameProfile = GameProfileFetcher.fetch(PoloCloudAPI.getInstance().getUuidFetcher().getUniqueId(skinName));
            Property property = gameProfile.getProperties().get("textures").iterator().next();
            String value = property.getValue();
            String signature = property.getSignature();

            npc.setSkin(new Skin(value, signature));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.npc.create();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            displayForPlayer(onlinePlayer);
        }
    }

    @Override
    public void remove() {
        if(this.npc != null){
            npc.destroy();
            this.npc = null;
        }
    }

    @Override
    public void update() {
        remove();
        spawn();
    }

    @Override
    public void displayForPlayer(Player player) {
        if (this.npc != null && !this.npc.isShown(player)) {
            this.npc.show(player);
        }
    }

    @Override
    public void hideForPlayer(Player player) {
        if (this.npc != null && this.npc.isShown(player)) {
            this.npc.hide(player);
        }
    }

    @Override
    public void reload(CloudNPCMeta metaData) {
        setCloudNPCMetaData(metaData);
        update();
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public Location getNativeLocation() {
        return new Location(this.location.getWorld(), this.location.getBlockX(), this.location.getBlockY(), this.location.getBlockZ());
    }

    @Override
    public NPC getNPC() {
        return this.npc;
    }

    @Override
    public Entity getEntity() {
        return null;
    }
}
