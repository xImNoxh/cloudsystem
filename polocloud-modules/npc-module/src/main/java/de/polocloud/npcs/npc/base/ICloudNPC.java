package de.polocloud.npcs.npc.base;

import de.polocloud.npcs.npc.base.meta.CloudNPCMeta;
import net.jitse.npclib.api.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface ICloudNPC {

    CloudNPCMeta getCloudNPCMetaData();

    void setCloudNPCMetaData(CloudNPCMeta metaData);

    void spawn();

    void remove();

    void update();

    void displayForPlayer(Player player);

    void hideForPlayer(Player player);

    void reload(CloudNPCMeta metaData);

    Location getLocation();

    Location getNativeLocation();

    NPC getNPC();

    Entity getEntity();

}
