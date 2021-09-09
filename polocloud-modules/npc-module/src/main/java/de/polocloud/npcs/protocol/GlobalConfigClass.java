package de.polocloud.npcs.protocol;

import de.polocloud.npcs.npc.base.meta.CloudNPCMeta;

import java.util.ArrayList;

/**
 * Class for transferring all NPCMeta's from the
 * PoloCloud-Module config to the Spigot/Bukkit servers
 */
public class GlobalConfigClass {

    private ArrayList<CloudNPCMeta> metas = new ArrayList<>();

    public ArrayList<CloudNPCMeta> getMetas() {
        return metas;
    }

    public void setMetas(ArrayList<CloudNPCMeta> metas) {
        this.metas = metas;
    }
}
