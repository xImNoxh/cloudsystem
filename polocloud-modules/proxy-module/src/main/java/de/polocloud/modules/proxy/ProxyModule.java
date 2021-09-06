package de.polocloud.modules.proxy;

import com.google.common.collect.Lists;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.module.CloudModule;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.modules.proxy.config.ProxyConfig;
import de.polocloud.modules.proxy.config.motd.ProxyMotdSettings;
import de.polocloud.modules.proxy.config.tablist.TablistConfig;
import de.polocloud.modules.proxy.events.CollectiveCloudListener;

import java.io.File;
import java.util.ArrayList;

public class ProxyModule {

    private static ProxyModule proxyModule;

    private CloudModule module;
    private ProxyConfig proxyConfig;

    public ProxyModule(CloudModule module) {
        proxyModule = this;
        this.module = module;

        loadConfig();
        saveConfig();
    }

    public void enable(){
        PoloCloudAPI.getInstance().getEventManager().registerListener(new CollectiveCloudListener());
        setupServices();
    }

    public void reload(){
        loadConfig();

        setupServices();
        sendAllTablist();
    }

    public void sendAllTablist(){
        TablistConfig tablistConfig = ProxyModule.getProxyModule().getProxyConfig().getTablistConfig();

        String header = replaceTabComponent(tablistConfig.getHeader());
        String footer = replaceTabComponent(tablistConfig.getFooter());

        Lists.newArrayList(PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached()).forEach(it ->
            it.sendTabList(
                header.replaceAll("%SERVICE%", it.getMinecraftServer().getName())
                .replaceAll("%MAX_PLAYERS%", String.valueOf(it.getProxyServer().getMaxPlayers())),
                footer.replaceAll("%SERVICE%", it.getMinecraftServer().getName())
                    .replaceAll("%MAX_PLAYERS%", String.valueOf(it.getProxyServer().getMaxPlayers()))));
    }

    public void loadConfig(){
        proxyConfig = new SimpleConfigLoader().load(ProxyConfig.class, new File(module.getDataDirectory(), "config.yml"));
    }

    public void saveConfig(){
        new SimpleConfigSaver().save(proxyConfig, new File(module.getDataDirectory(), "config.yml"));
    }

    public CloudModule getModule() {
        return module;
    }

    public void setupServices(){
        PoloCloudAPI.getInstance().getGameServerManager().getCached(TemplateType.PROXY).forEach(it -> sendMotd(it));
    }

    public String replaceTabComponent(String value){
        return value.replaceAll("%ONLINE_PLAYERS%", String.valueOf(PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached().size()));
    }

    public void sendMotd(IGameServer server){
        server.setMotd(server.getTemplate().isMaintenance() ? getMaintenanceMotd() : getOnlineMotd());
    }

    public String getMaintenanceMotd(){
        return getProxySetting().getMaintenanceMotd().getFirstLine() + "\n" + getProxySetting().getMaintenanceMotd().getSecondLine();
    }

    public String getOnlineMotd(){
        return getProxySetting().getOnlineMotd().getFirstLine() + "\n" + getProxySetting().getOnlineMotd().getSecondLine();
    }

    public ProxyMotdSettings getProxySetting(){
        return proxyConfig.getProxyMotdSettings();
    }

    public ProxyConfig getProxyConfig() {
        return proxyConfig;
    }

    public static ProxyModule getProxyModule() {
        return proxyModule;
    }
}
