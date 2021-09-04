package de.polocloud.signs.module;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.api.module.CloudModule;
import de.polocloud.api.util.WrappedObject;
import de.polocloud.signs.module.config.layout.SignLayoutConfig;
import de.polocloud.signs.module.config.location.SignLocationConfig;
import de.polocloud.signs.protocol.enumeration.RequestType;
import de.polocloud.signs.service.ISignService;
import de.polocloud.signs.module.config.SignConfig;
import de.polocloud.signs.protocol.GlobalConfigClass;

import java.io.File;

public class ModuleSignService implements ISignService {

    /*
       Instance
     */
    private CloudModule module;

    /*
       Configs
     */
    private SignConfig mainConfig;
    private SignLocationConfig locationConfig;
    private SignLayoutConfig layoutConfig;

    /*
       Objects for syncing Configs
     */
    private GlobalConfigClass currentGlobalConfig;
    private IMessageChannel<WrappedObject<GlobalConfigClass>> channel;
    private IMessageChannel<WrappedObject<RequestType>> requestChannel;


    public ModuleSignService(CloudModule module) {
        this.module = module;

        this.currentGlobalConfig = new GlobalConfigClass();

        //Loading configs
        loadConfig();
        saveConfig();
        updateCurrentGlobalConfig();

        //Inits the channels and registers the Listeners
        PoloCloudAPI.getInstance().getMessageManager().registerChannel(WrappedObject.class, "sign-transfer-channel");
        PoloCloudAPI.getInstance().getMessageManager().registerChannel(WrappedObject.class, "sign-request-channel");
        this.channel = PoloCloudAPI.getInstance().getMessageManager().getChannel("sign-transfer-channel");
        this.requestChannel = PoloCloudAPI.getInstance().getMessageManager().getChannel("sign-request-channel");
        registerListeners();
    }

    @Override
    public void loadSigns() {
        //Reloads the configs and sets the GlobalConfig
        reloadConfig();
        updateCurrentGlobalConfig();
    }

    @Override
    public void reloadSigns() {
        //Reloads the configs and sends them over the channel to the servers
        loadConfig();
        updateSigns();
    }

    @Override
    public void updateSigns() {
        //Send signs to channel
        channel.sendMessage(new WrappedObject<>(currentGlobalConfig));
    }

    @Override
    public void registerListeners() {
        //Channel for receiving new Signs from the Server
        this.channel.registerListener((globalTransferClassWrappedObject, startTime) -> {
            GlobalConfigClass receivedGlobalConfigClass = globalTransferClassWrappedObject.unwrap(GlobalConfigClass.class);

            if(receivedGlobalConfigClass != null){
                currentGlobalConfig = receivedGlobalConfigClass;
                locationConfig.setLocations(receivedGlobalConfigClass.getLocations());
                saveConfig();
            }
        });

        //Channel for requests from the Server to send the Signs (usually on startup of the SignPlugin on the server)
        this.requestChannel.registerListener((requestTypeWrappedObject, startTime) -> {
            RequestType requestedType = requestTypeWrappedObject.unwrap(RequestType.class);
            if(requestedType != null && requestedType.equals(RequestType.ALL)){
                updateSigns();
            }
        });
    }

    public void loadConfig(){
        IConfigLoader configLoader = new SimpleConfigLoader();
        mainConfig = configLoader.load(SignConfig.class, new File(module.getDataDirectory(), "config.json"));
        layoutConfig = configLoader.load(SignLayoutConfig.class, new File(module.getDataDirectory(), "layout.json"));
        locationConfig = configLoader.load(SignLocationConfig.class, new File(module.getDataDirectory(), "locations.json"));
    }

    public void saveConfig(){
        IConfigSaver configSaver = new SimpleConfigSaver();
        configSaver.save(mainConfig, new File(module.getDataDirectory(), "config.json"));
        configSaver.save(layoutConfig, new File(module.getDataDirectory(), "layout.json"));
        configSaver.save(locationConfig, new File(module.getDataDirectory(), "locations.json"));
    }

    public void updateCurrentGlobalConfig(){
        if(currentGlobalConfig == null){
            currentGlobalConfig = new GlobalConfigClass();
        }
        currentGlobalConfig.setSignLayouts(layoutConfig.getSignLayouts());
        currentGlobalConfig.setSignMessages(mainConfig.getSignMessages());
        currentGlobalConfig.setLocations(locationConfig.getLocations());
    }


    public void reloadConfig(){
        loadConfig();
        updateCurrentGlobalConfig();
    }

    public CloudModule getModule() {
        return module;
    }

    public SignConfig getMainConfig() {
        return mainConfig;
    }

    public SignLocationConfig getLocationConfig() {
        return locationConfig;
    }

    public SignLayoutConfig getLayoutConfig() {
        return layoutConfig;
    }

    public GlobalConfigClass getCurrentGlobalConfig() {
        return currentGlobalConfig;
    }
}
