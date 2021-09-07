package de.polocloud.modules.proxy.whitelist;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.modules.proxy.whitelist.channel.WhitelistChannel;
import de.polocloud.modules.proxy.whitelist.events.WhitelistCollectiveEvents;
import de.polocloud.modules.proxy.whitelist.property.WhitelistProperty;

public class WhitelistProxyService {

    private static WhitelistProxyService instance;
    private WhitelistChannel whitelistChannel;
    private WhitelistProperty whitelistProperty;

    public WhitelistProxyService() {
        instance = this;

        this.whitelistChannel = new WhitelistChannel();

        whitelistChannel.getChannel().registerListener((globalConfigClassWrappedObject, startTime) -> {
            whitelistProperty = globalConfigClassWrappedObject.unwrap(WhitelistProperty.class);
        });

        PoloCloudAPI.getInstance().getEventManager().registerListener(new WhitelistCollectiveEvents());
    }

    public WhitelistProperty getWhitelistProperty() {
        return whitelistProperty;
    }

    public static WhitelistProxyService getInstance() {
        return instance;
    }

    public WhitelistChannel getWhitelistChannel() {
        return whitelistChannel;
    }
}
