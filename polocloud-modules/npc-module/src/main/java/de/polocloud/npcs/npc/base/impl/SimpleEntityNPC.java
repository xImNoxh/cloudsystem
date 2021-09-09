package de.polocloud.npcs.npc.base.impl;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.npcs.bootstraps.PluginBootstrap;
import de.polocloud.npcs.npc.base.ICloudNPC;
import de.polocloud.npcs.npc.base.meta.CloudNPCMeta;
import de.polocloud.npcs.npc.entity.EntityProvider;
import de.polocloud.npcs.npc.entity.nms.NMSEntity;
import de.polocloud.npcs.npc.entity.nms.nbt.NMSNBTTag;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.hologram.Hologram;
import net.jitse.npclib.internal.MinecraftVersion;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public class SimpleEntityNPC implements ICloudNPC {

    private CloudNPCMeta meta;

    private Location location;
    private Entity entity;
    private EntityType entityType;

    private Hologram hologram;


    public SimpleEntityNPC(CloudNPCMeta meta) {
        this.meta = meta;

        this.location = new Location(Bukkit.getWorld(meta.getWorld()), meta.getX(), meta.getY(), meta.getZ());

        this.entityType = EntityProvider.getEntityTypeByName(meta.getEntityName());
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
        if(this.entityType == null){
            return;
        }

        if(location.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)){
            Bukkit.getLogger().log(Level.WARNING, "[CloudNPCs] The difficulty in world -> " + location.getWorld().getName() + " is PEACEFUL, some entities cannot spawn in the PEACEFUL state!" +
                " Please check you difficulty settings!" +
                " Setting world to EASY...");
            location.getWorld().setDifficulty(Difficulty.EASY);
        }
        Bukkit.getScheduler().runTask(PluginBootstrap.getInstance(), () ->{
            this.entity = location.getWorld().spawnEntity(location, entityType);
            NMSEntity nmsEntity = new NMSEntity(entity);
            NMSNBTTag nmsNBTTag = new NMSNBTTag();
            nmsEntity.initNBTTag(nmsNBTTag);
            nmsNBTTag.setInt("NoAI", 1);
            nmsEntity.setNBTTag(nmsNBTTag);
            this.entity = nmsEntity.getAsBukkitEntity();

            this.entity.setFireTicks(0);
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
            this.hologram = new Hologram(MinecraftVersion.valueOf(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].toUpperCase()), entity.getLocation().add(new Vector(0, 0.5, 0)), hologramLines);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                this.hologram.show(onlinePlayer);
            }
        });

    }

    @Override
    public void remove() {
        if(this.entity != null && !this.entity.isDead()){
            this.entity.remove();
            this.entity = null;
            if(hologram != null){
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    hologram.hide(onlinePlayer);
                }
            }
        }
    }

    @Override
    public void displayForPlayer(Player player){
        if(this.hologram != null){
            this.hologram.show(player);
        }
    }

    @Override
    public void hideForPlayer(Player player) {
        if(this.hologram != null){
            this.hologram.hide(player);
        }
    }

    @Override
    public void update() {
        remove();
        spawn();
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
    public NPC getNPC() {
        return null;
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }
}
