package de.polocloud.npcs.manager;

import de.polocloud.npcs.npc.base.ICloudNPC;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public interface ICloudNPCManager {

    ArrayList<ICloudNPC> getCloudNPCS();

    ICloudNPC getNPCByLocation(Location location);

    ICloudNPC getNPCByEntityID(int id);

    List<ICloudNPC> getAllEntityNPCs();

    List<ICloudNPC> getAllCloudNPCs();

    List<ICloudNPC> getAllNPCsFromTemplateOrGameServerName(String nae);


    void setCloudNPCS(ArrayList<ICloudNPC> cloudNPCS);
}
