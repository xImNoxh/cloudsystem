package de.polocloud.modules.proxy.whitelist.channel;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.api.util.WrappedObject;
import de.polocloud.modules.proxy.whitelist.property.WhitelistProperty;

public class WhitelistChannel {

    private IMessageChannel<WrappedObject<WhitelistProperty>> channel;

    public WhitelistChannel() {
        PoloCloudAPI.getInstance().getMessageManager().registerChannel(WrappedObject.class, "whitelist-channel");
        this.channel = PoloCloudAPI.getInstance().getMessageManager().getChannel("whitelist-channel");
    }

    public IMessageChannel<WrappedObject<WhitelistProperty>> getChannel() {
        return channel;
    }

}
