package de.polocloud.modules.proxy.motd.channel;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.api.util.WrappedObject;
import de.polocloud.modules.proxy.motd.properties.MotdVersionProperty;

public class MotdVersionChannel {

    private IMessageChannel<WrappedObject<MotdVersionProperty>> channel;

    public MotdVersionChannel() {
        PoloCloudAPI.getInstance().getMessageManager().registerChannel(WrappedObject.class, "motd-version-channel");
        this.channel = PoloCloudAPI.getInstance().getMessageManager().getChannel("motd-version-channel");
    }

    public IMessageChannel<WrappedObject<MotdVersionProperty>> getChannel() {
        return channel;
    }
}
