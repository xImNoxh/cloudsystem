package de.polocloud.modules.proxy.motd;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.util.WrappedObject;
import de.polocloud.modules.proxy.IProxyReload;
import de.polocloud.modules.proxy.ProxyConfig;
import de.polocloud.modules.proxy.ProxyModule;
import de.polocloud.modules.proxy.motd.channel.MotdVersionChannel;
import de.polocloud.modules.proxy.motd.config.ProxyMotdSettings;
import de.polocloud.modules.proxy.motd.events.ServiceMotdEvents;
import de.polocloud.modules.proxy.motd.properties.MotdVersionProperty;

public class MotdService implements IProxyReload {

    private static MotdService instance;
    private ProxyConfig proxyConfig;
    private MotdVersionChannel motdVersionChannel;

    public MotdService() {
        instance = this;

        this.motdVersionChannel = new MotdVersionChannel();
        this.proxyConfig = ProxyModule.getProxyModule().getProxyConfig();

        PoloCloudAPI.getInstance().getEventManager().registerListener(new ServiceMotdEvents());
    }

    public void sendAllProxiesMotd(){
        PoloCloudAPI.getInstance().getGameServerManager().getAllCached(TemplateType.PROXY).forEach(this::sendMotd);
    }

    public void sendMotd(IGameServer server){
        server.setMotd(server.getTemplate().isMaintenance() ? getMaintenanceMotd() : getOnlineMotd());
    }

    public ProxyMotdSettings getProxySetting(){
        return proxyConfig.getProxyMotdSettings();
    }

    public String getMaintenanceMotd(){
        return getProxySetting().getMaintenanceMotd().getFirstLine() + "\n" + getProxySetting().getMaintenanceMotd().getSecondLine();
    }

    public String getOnlineMotd(){
        return getProxySetting().getOnlineMotd().getFirstLine() + "\n" + getProxySetting().getOnlineMotd().getSecondLine();
    }

    @Override
    public void onReload() {
        this.proxyConfig = ProxyModule.getProxyModule().getProxyConfig();
        sendAllProxiesMotd();
        updateVersionTag();
    }

    public static MotdService getInstance() {
        return instance;
    }

    public MotdVersionChannel getMotdVersionChannel() {
        return motdVersionChannel;
    }

    public void updateVersionTag(){
        MotdVersionProperty motdVersionProperty = new MotdVersionProperty(
            proxyConfig.getProxyMotdSettings().getOnlineMotd().getVersionTag(),
            proxyConfig.getProxyMotdSettings().getMaintenanceMotd().getVersionTag());
        motdVersionChannel.getChannel().sendMessage(new WrappedObject<>(motdVersionProperty));
    }

}
