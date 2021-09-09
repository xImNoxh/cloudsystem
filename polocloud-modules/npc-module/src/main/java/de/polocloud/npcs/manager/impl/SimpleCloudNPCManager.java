package de.polocloud.npcs.manager.impl;

import de.polocloud.npcs.manager.ICloudNPCManager;
import de.polocloud.npcs.npc.base.ICloudNPC;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleCloudNPCManager implements ICloudNPCManager {

    private ArrayList<ICloudNPC> cloudNPCS;

    public SimpleCloudNPCManager() {
        this.cloudNPCS = new ArrayList<>();
    }

    @Override
    public ICloudNPC getNPCByLocation(Location location) {
        return this.cloudNPCS.stream().filter(npc -> npc.getLocation().equals(location)).findFirst().orElse(null);
    }

    @Override
    public List<ICloudNPC> getAllEntityNPCs() {
        return this.cloudNPCS.stream().filter(npc -> npc.getCloudNPCMetaData().isEntity()).collect(Collectors.toList());
    }

    @Override
    public List<ICloudNPC> getAllCloudNPCs() {
        return this.cloudNPCS.stream().filter(npc -> !npc.getCloudNPCMetaData().isEntity()).collect(Collectors.toList());
    }

    @Override
    public ICloudNPC getNPCByEntityID(int id) {
        return getAllEntityNPCs().stream().filter(entity -> entity.getEntity().getEntityId() == id).findFirst().orElse(null);
    }

    @Override
    public List<ICloudNPC> getAllNPCsFromTemplateOrGameServerName(String gameserverName) {
        return cloudNPCS.stream().filter(npc -> npc.getCloudNPCMetaData().getTemplateOrGameServer().equals(gameserverName)).collect(Collectors.toList());
    }

    @Override
    public ArrayList<ICloudNPC> getCloudNPCS() {
        return cloudNPCS;
    }

    @Override
    public void setCloudNPCS(ArrayList<ICloudNPC> cloudNPCS) {
        this.cloudNPCS = cloudNPCS;
    }
}
