package de.polocloud.modules.proxy.whitelist.channel;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.api.util.WrappedObject;
import de.polocloud.modules.proxy.whitelist.property.WhitelistProperty;

public class WhitelistChannel {

    private IMessageChannel<WrappedObject<WhitelistProperty>> channel;
    private IMessageChannel<WrappedObject<WhitelistProperty>> updateChannel;

    public WhitelistChannel() {
        PoloCloudAPI.getInstance().getMessageManager().registerChannel(WrappedObject.class, "whitelist-channel");
        this.channel = PoloCloudAPI.getInstance().getMessageManager().getChannel("whitelist-channel");

        PoloCloudAPI.getInstance().getMessageManager().registerChannel(WrappedObject.class, "whitelist-update-channel");
        this.updateChannel = PoloCloudAPI.getInstance().getMessageManager().getChannel("whitelist-update");
    }

    public IMessageChannel<WrappedObject<WhitelistProperty>> getUpdateChannel() {
        return updateChannel;
    }

    public IMessageChannel<WrappedObject<WhitelistProperty>> getChannel() {
        return channel;
    }

}
