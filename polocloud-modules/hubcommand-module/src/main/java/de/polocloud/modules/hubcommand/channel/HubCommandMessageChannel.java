package de.polocloud.modules.hubcommand.channel;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.api.util.WrappedObject;
import de.polocloud.modules.hubcommand.config.HubCommandConfig;

public class HubCommandMessageChannel {

    private IMessageChannel<HubCommandConfig> messageChannel;

    public HubCommandMessageChannel() {
        PoloCloudAPI.getInstance().getMessageManager().registerChannel(HubCommandConfig.class, "hubcommand-message-channel");
        this.messageChannel = PoloCloudAPI.getInstance().getMessageManager().getChannel("hubcommand-message-channel");
    }

    public IMessageChannel<HubCommandConfig> getMessageChannel() {
        return messageChannel;
    }
}
