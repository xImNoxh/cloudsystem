package de.polocloud.npcs.module.config.meta;

import de.polocloud.api.config.IConfig;
import de.polocloud.npcs.npc.base.meta.CloudNPCMeta;

import java.util.ArrayList;

public class NPCMeta implements IConfig {

    private ArrayList<CloudNPCMeta> metas = new ArrayList<>();

    public ArrayList<CloudNPCMeta> getMetas() {
        return metas;
    }

    public void setMetas(ArrayList<CloudNPCMeta> metas) {
        this.metas = metas;
    }
}
