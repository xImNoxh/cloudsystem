package de.polocloud.modules.proxy.whitelist;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.util.WrappedObject;
import de.polocloud.modules.proxy.IProxyReload;
import de.polocloud.modules.proxy.ProxyModule;
import de.polocloud.modules.proxy.whitelist.channel.WhitelistChannel;
import de.polocloud.modules.proxy.whitelist.property.WhitelistProperty;

public class WhitelistService implements IProxyReload {

    private WhitelistService instance;
    private WhitelistChannel whitelistChannel;

    public WhitelistService() {
        this.instance = this;

        this.whitelistChannel = new WhitelistChannel();

        this.whitelistChannel.getUpdateChannel().registerListener((whitelistPropertyWrappedObject, startTime) -> {
            updateWhitelist();
        });

    }

    public WhitelistChannel getWhitelistChannel() {
        return whitelistChannel;
    }

    public WhitelistService getInstance() {
        return instance;
    }

    public void updateWhitelist(){
        this.whitelistChannel.getChannel().sendMessage((new WrappedObject<>(new WhitelistProperty(
            ProxyModule.getProxyModule().getProxyConfig().getWhitelist().getWhitelistPlayers()))));
    }



    @Override
    public void onReload() {
        updateWhitelist();
    }
}
