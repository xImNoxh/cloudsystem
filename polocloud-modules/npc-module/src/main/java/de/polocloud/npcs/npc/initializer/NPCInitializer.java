package de.polocloud.npcs.npc.initializer;

import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.npcs.bootstraps.PluginBootstrap;
import de.polocloud.npcs.npc.base.ICloudNPC;
import de.polocloud.npcs.npc.base.impl.SimpleCloudNPC;
import de.polocloud.npcs.npc.base.impl.SimpleEntityNPC;
import de.polocloud.npcs.npc.base.meta.CloudNPCMeta;

public class NPCInitializer {

    public void loadNPCs(){
        for (ICloudNPC cloudNPC : PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getCloudNPCS()) {
            cloudNPC.remove();
        }
        PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getCloudNPCS().clear();

        Scheduler.runtimeScheduler().schedule(() -> Scheduler.runtimeScheduler().schedule(() ->{
            for (CloudNPCMeta meta : PluginBootstrap.getInstance().getNpcService().getCurrentCache().getMetas()) {
                ICloudNPC npc;
                if(meta.isEntity()){
                    npc = new SimpleEntityNPC(meta);
                }else{
                    npc = new SimpleCloudNPC(meta);
                }
                npc.spawn();
                PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getCloudNPCS().add(npc);
            }
        }, 25),() -> PluginBootstrap.getInstance().getNpcService().getCurrentCache() != null);
    }

}
