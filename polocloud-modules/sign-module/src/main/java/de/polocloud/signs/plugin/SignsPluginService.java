package de.polocloud.signs.plugin;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.util.WrappedObject;
import de.polocloud.signs.protocol.enumeration.RequestType;
import de.polocloud.signs.sign.base.IGameServerSign;
import de.polocloud.signs.manager.IGameServerSignManager;
import de.polocloud.signs.manager.impl.SimpleGameServerSignManager;
import de.polocloud.signs.protocol.GlobalConfigClass;
import de.polocloud.signs.service.ISignService;
import de.polocloud.signs.sign.animator.SignAnimator;
import de.polocloud.signs.sign.initializer.SignInitializer;
import de.polocloud.signs.sign.layout.converter.SignConverter;
import de.polocloud.signs.sign.location.SignLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SignsPluginService implements ISignService {

    /*
       Objects for syncing Configs
     */
    private GlobalConfigClass currentGlobalConfig;
    private IMessageChannel<WrappedObject<GlobalConfigClass>> channel;
    private IMessageChannel<WrappedObject<RequestType>> requestChannel;

    private IGameServerSignManager gameServerSignManager;
    private SignInitializer signInitializer;
    private SignAnimator signAnimator;

    public SignsPluginService() {


        //Initializes the channels and registers the Listeners
        PoloCloudAPI.getInstance().getMessageManager().registerChannel(WrappedObject.class, "sign-transfer-channel");
        PoloCloudAPI.getInstance().getMessageManager().registerChannel(WrappedObject.class, "sign-request-channel");
        this.channel = PoloCloudAPI.getInstance().getMessageManager().getChannel("sign-transfer-channel");
        this.requestChannel = PoloCloudAPI.getInstance().getMessageManager().getChannel("sign-request-channel");

        this.gameServerSignManager = new SimpleGameServerSignManager();
        this.signInitializer = new SignInitializer();
        this.signAnimator = new SignAnimator();
        SignConverter signConverter = new SignConverter();

        registerListeners();
        Scheduler.runtimeScheduler().async().schedule(this::requestUpdate, () -> PoloCloudAPI.getInstance().getConnection().ctx() != null);
    }

    @Override
    public void loadSigns() {
        Scheduler.runtimeScheduler().schedule(() -> signInitializer.initSigns(), () -> this.currentGlobalConfig != null);
    }

    @Override
    public void reloadSigns() {
        requestUpdate();
    }

    @Override
    public void updateSigns() {
        List<SignLocation> toUpdate = gameServerSignManager.getLoadedSigns().stream().map(IGameServerSign::getSignLocation).collect(Collectors.toList());
        currentGlobalConfig.setLocations(new ArrayList<>(toUpdate));
        channel.sendMessage(new WrappedObject<>(currentGlobalConfig));
    }

    @Override
    public void registerListeners() {
        channel.registerListener((globalConfigClassWrappedObject, startTime) -> {
            currentGlobalConfig = globalConfigClassWrappedObject.unwrap(GlobalConfigClass.class);
            loadSigns();
        });
    }

    public void requestUpdate(){
        if(this.requestChannel != null){
            this.requestChannel.sendMessage(new WrappedObject<>(RequestType.ALL));
        }
    }

    public IGameServerSignManager getGameServerSignManager() {
        return gameServerSignManager;
    }

    public GlobalConfigClass getCurrentGlobalConfig() {
        return currentGlobalConfig;
    }

    public SignInitializer getSignInitializer() {
        return signInitializer;
    }

    public SignAnimator getSignAnimator() {
        return signAnimator;
    }
}
