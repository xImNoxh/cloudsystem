package de.polocloud.npcs.plugin;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.util.WrappedObject;
import de.polocloud.npcs.bootstraps.PluginBootstrap;
import de.polocloud.npcs.manager.ICloudNPCManager;
import de.polocloud.npcs.manager.impl.SimpleCloudNPCManager;
import de.polocloud.npcs.npc.base.ICloudNPC;
import de.polocloud.npcs.npc.base.meta.CloudNPCMeta;
import de.polocloud.npcs.npc.entity.nms.wrapper.NMSClassWrapper;
import de.polocloud.npcs.npc.initializer.NPCInitializer;
import de.polocloud.npcs.protocol.GlobalConfigClass;
import de.polocloud.npcs.protocol.enumeration.RequestType;
import de.polocloud.npcs.service.INPCService;
import net.jitse.npclib.NPCLib;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PluginNPCService implements INPCService {

    private NPCLib npcLib;


    private ICloudNPCManager cloudNPCManager;

    private GlobalConfigClass currentCache;

    private IMessageChannel<WrappedObject<GlobalConfigClass>> transferChannel;
    private IMessageChannel<WrappedObject<RequestType>> requestChannel;

    private NPCInitializer initializer;


    public PluginNPCService() {
        NMSClassWrapper.loadClasses();
        npcLib = new NPCLib(PluginBootstrap.getInstance());


        this.cloudNPCManager = new SimpleCloudNPCManager();
        this.initializer = new NPCInitializer();

        PoloCloudAPI.getInstance().getMessageManager().registerChannel(WrappedObject.class, "npc-transfer-channel");
        PoloCloudAPI.getInstance().getMessageManager().registerChannel(WrappedObject.class, "npc-request-channel");
        this.transferChannel = PoloCloudAPI.getInstance().getMessageManager().getChannel("npc-transfer-channel");
        this.requestChannel = PoloCloudAPI.getInstance().getMessageManager().getChannel("npc-request-channel");

        Scheduler.runtimeScheduler().schedule(() ->{
            registerListeners();
            requestUpdate();
        }, () -> PoloCloudAPI.getInstance().getConnection().ctx() != null);
    }

    @Override
    public void loadNPCs() {
        this.initializer.loadNPCs();
    }

    @Override
    public void reloadNPCs() {
        requestUpdate();
    }

    @Override
    public void updateNPCs() {
        List<CloudNPCMeta> metas = this.cloudNPCManager.getCloudNPCS().stream().map(ICloudNPC::getCloudNPCMetaData).collect(Collectors.toList());
        this.currentCache.setMetas(new ArrayList<>(metas));
        this.transferChannel.sendMessage(new WrappedObject<>(this.currentCache));
    }

    @Override
    public void registerListeners() {
        this.transferChannel.registerListener(((globalConfigClassWrappedObject, startTime) -> {
            this.currentCache = globalConfigClassWrappedObject.unwrap(GlobalConfigClass.class);
            loadNPCs();
        }));
    }

    public void requestUpdate(){
        if (this.requestChannel != null) {
            this.requestChannel.sendMessage(new WrappedObject<>(RequestType.ALL));
        }
    }

    public NPCLib getNpcLib() {
        return npcLib;
    }

    public GlobalConfigClass getCurrentCache() {
        return currentCache;
    }

    public ICloudNPCManager getCloudNPCManager() {
        return cloudNPCManager;
    }
}
