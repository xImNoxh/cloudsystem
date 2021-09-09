package de.polocloud.npcs.module;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.api.module.CloudModule;
import de.polocloud.api.util.WrappedObject;
import de.polocloud.npcs.module.config.meta.NPCMeta;
import de.polocloud.npcs.protocol.GlobalConfigClass;
import de.polocloud.npcs.protocol.enumeration.RequestType;
import de.polocloud.npcs.service.INPCService;

import java.io.File;

public class ModuleNPCService implements INPCService {

    private CloudModule module;

    private NPCMeta metaConfig;

    private GlobalConfigClass currentCache;

    private IMessageChannel<WrappedObject<GlobalConfigClass>> transferChannel;
    private IMessageChannel<WrappedObject<RequestType>> requestChannel;

    public ModuleNPCService(CloudModule module) {
        this.module = module;
        loadConfigs();
        saveConfigs();
        setCurrentCache();

        PoloCloudAPI.getInstance().getMessageManager().registerChannel(WrappedObject.class, "npc-transfer-channel");
        PoloCloudAPI.getInstance().getMessageManager().registerChannel(WrappedObject.class, "npc-request-channel");
        this.transferChannel = PoloCloudAPI.getInstance().getMessageManager().getChannel("npc-transfer-channel");
        this.requestChannel = PoloCloudAPI.getInstance().getMessageManager().getChannel("npc-request-channel");
        registerListeners();
    }

    @Override
    public void loadNPCs() {
        loadConfigs();
        saveConfigs();
        setCurrentCache();
    }

    @Override
    public void reloadNPCs() {
        loadConfigs();
        updateNPCs();
    }

    @Override
    public void updateNPCs() {
        this.transferChannel.sendMessage(new WrappedObject<>(this.currentCache));
    }

    @Override
    public void registerListeners() {
        this.requestChannel.registerListener(((requestTypeWrappedObject, startTime) -> {
            if(requestTypeWrappedObject.unwrap(RequestType.class).equals(RequestType.ALL)){
                updateNPCs();
            }
        }));
        this.transferChannel.registerListener(((globalConfigClassWrappedObject, startTime) -> {
            GlobalConfigClass newGlobalConfigClass = globalConfigClassWrappedObject.unwrap(GlobalConfigClass.class);
            if (newGlobalConfigClass != null) {
                currentCache = newGlobalConfigClass;
                this.metaConfig.setMetas(newGlobalConfigClass.getMetas());
                saveConfigs();
            }
        }));
    }

    public void loadConfigs(){
        IConfigLoader configLoader = new SimpleConfigLoader();
        metaConfig = configLoader.load(NPCMeta.class, new File(module.getDataDirectory(), "npcs.json"));
    }

    public void saveConfigs(){
        IConfigSaver configSaver = new SimpleConfigSaver();
        configSaver.save(metaConfig, new File(module.getDataDirectory(), "npcs.json"));
    }

    public void setCurrentCache(){
        if (this.currentCache == null) {
            this.currentCache = new GlobalConfigClass();
        }
        this.currentCache.setMetas(this.metaConfig.getMetas());
    }

    public void setCurrentCache(GlobalConfigClass currentCache) {
        this.currentCache = currentCache;
    }
}
