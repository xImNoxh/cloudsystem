package de.polocloud.modules.hubcommand.channel;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.api.util.WrappedObject;
import de.polocloud.modules.hubcommand.config.HubCommandConfig;

public class HubCommandMessageChannel {

    private IMessageChannel<WrappedObject<HubCommandConfig>> messageChannel;

    public HubCommandMessageChannel() {
        PoloCloudAPI.getInstance().getMessageManager().registerChannel(WrappedObject.class, "hubcommand-message-channel");
        this.messageChannel = PoloCloudAPI.getInstance().getMessageManager().getChannel("hubcommand-message-channel");
    }

    public IMessageChannel<WrappedObject<HubCommandConfig>> getMessageChannel() {
        return messageChannel;
    }
}
